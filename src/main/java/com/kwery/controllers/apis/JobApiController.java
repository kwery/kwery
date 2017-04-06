package com.kwery.controllers.apis;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.kwery.conf.KweryDirectory;
import com.kwery.controllers.MessageKeys;
import com.kwery.dao.*;
import com.kwery.dao.search.JobSearchDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.dtos.*;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.*;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.utils.CsvReaderFactory;
import com.kwery.utils.KweryUtil;
import com.kwery.utils.ReportUtil;
import com.kwery.views.ActionResult;
import it.sauronsoftware.cron4j.Predictor;
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.TaskExecutor;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.utils.KweryUtil.fileName;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ninja.Result.SC_200_OK;
import static ninja.Result.SC_404_NOT_FOUND;
import static ninja.Results.json;

public class JobApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DISPLAY_DATE_FORMAT = "EEE MMM dd yyyy HH:mm";

    protected final DatasourceDao datasourceDao;
    protected final JobDao jobDao;
    protected final JobService jobService;
    protected final JobExecutionDao jobExecutionDao;
    protected final SqlQueryDao sqlQueryDao;
    protected final Messages messages;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final JobLabelDao jobLabelDao;
    protected final KweryDirectory kweryDirectory;
    protected final CsvReaderFactory csvReaderFactory;
    protected final JobSearchDao jobSearchDao;

    @Inject
    public JobApiController(DatasourceDao datasourceDao, JobDao jobDao, JobService jobService, JobExecutionDao jobExecutionDao, SqlQueryDao sqlQueryDao,
                            SqlQueryExecutionDao sqlQueryExecutionDao, JobLabelDao jobLabelDao, JobSearchDao jobSearchDao, KweryDirectory kweryDirectory, Messages messages,
                            CsvReaderFactory csvReaderFactory) {
        this.datasourceDao = datasourceDao;
        this.jobDao = jobDao;
        this.jobService = jobService;
        this.jobExecutionDao = jobExecutionDao;
        this.sqlQueryDao = sqlQueryDao;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.jobLabelDao = jobLabelDao;
        this.kweryDirectory = kweryDirectory;
        this.messages = messages;
        this.csvReaderFactory = csvReaderFactory;
        this.jobSearchDao = jobSearchDao;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result saveJob(JobDto jobDto, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");

        boolean isUpdate = jobDto.getId() > 0;

        Result json = json();

        List<String> errorMessages = new LinkedList<>();

        if (jobDto.getParentJobId() == 0 && !SchedulingPattern.validate(jobDto.getCronExpression())) {
            String message = messages.get(JOBLABELAPICONTROLLER_INVALID_CRON_EXPRESSION, context, Optional.of(json), jobDto.getCronExpression()).get();
            errorMessages.add(message);
        }

        JobModel jobByLabel = jobDao.getJobByName(jobDto.getName());
        if (jobByLabel != null) {
            if (isUpdate) {
                if (!jobByLabel.getId().equals(jobDto.getId())) {
                    String message = messages.get(JOBAPICONTROLLER_REPORT_NAME_EXISTS, context, Optional.of(json), jobDto.getName()).get();
                    errorMessages.add(message);
                }
            } else {
                String message = messages.get(JOBAPICONTROLLER_REPORT_NAME_EXISTS, context, Optional.of(json), jobDto.getName()).get();
                errorMessages.add(message);
            }
        }

        for (SqlQueryDto sqlQueryDto : jobDto.getSqlQueries()) {
            SqlQueryModel byLabel = sqlQueryDao.getByLabel(sqlQueryDto.getLabel());
            if (byLabel != null) {
                if (isUpdate) {
                    if (!byLabel.getId().equals(sqlQueryDto.getId())) {
                        String message = messages.get(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS, context, Optional.of(json), sqlQueryDto.getLabel()).get();
                        errorMessages.add(message);
                    }
                } else {
                    String message = messages.get(JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS, context, Optional.of(json), sqlQueryDto.getLabel()).get();
                    errorMessages.add(message);
                }
            }
        }

        ActionResult actionResult = null;

        if (errorMessages.isEmpty()) {
            JobModel jobModel = jobDtoToJobModel(jobDto);
            jobModel.setChildJobs(new HashSet<>());

            if (isUpdate) {
                JobModel jobFromDb = jobDao.getJobById(jobDto.getId());
                jobModel.getChildJobs().addAll(jobFromDb.getChildJobs());
                if (jobDto.getParentJobId() > 0) {
                    //They are mutually exclusive
                    jobModel.setCronExpression(null);
                }

                //Deschedule if it was a scheduled job earlier
                if (!"".equals(Strings.nullToEmpty(jobFromDb.getCronExpression()))) {
                    jobService.deschedule(jobModel.getId());
                }
            }

            jobModel = jobDao.save(jobModel);

            if (jobDto.getParentJobId() > 0) {
                JobModel parentJob = jobDao.getJobById(jobDto.getParentJobId());
                parentJob.getChildJobs().add(jobModel);
                jobDao.save(parentJob);
            } else {
                jobService.schedule(jobModel.getId());
            }

            actionResult = new ActionResult(success, "");
        } else {
            actionResult = new ActionResult(failure, errorMessages);
        }

        if (logger.isTraceEnabled()) logger.trace(">");
        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result deleteJob(@PathParam("jobId") int jobId, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobModel jobModel = jobDao.getJobById(jobId);

        Result json = json();
        Result result = null;

        if (!jobModel.getChildJobs().isEmpty()) {
            String message = messages.get(JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN, context, Optional.of(json)).get();
            result = json.render(new ActionResult(failure, message));
        } else {
            //Is this a scheduled job?
            if (!Strings.nullToEmpty(jobModel.getCronExpression()).equals("")) {
                jobService.deschedule(jobId);
            }
            jobDao.delete(jobId);
            result = json.render(new ActionResult(success, ""));
        }

        if (logger.isTraceEnabled()) logger.trace(">");
        return result;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result executeJob(@PathParam("jobId") int jobId) {
        TaskExecutor taskExecutor = jobService.launch(jobId);
        return json().render(ImmutableMap.of("executionId", taskExecutor.getGuid()));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listExecutingJobs() {
        if (logger.isTraceEnabled()) logger.trace("<");
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(JobExecutionModel.Status.ONGOING));
        List<JobExecutionModel> jobExecutionModels = jobExecutionDao.filter(filter);
        List<JobExecutionDto> dtos = jobExecutionModels.stream().map(this::jobExecutionModelToJobExecutionDto).collect(toList());
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listJobs(JobListFilterDto filterDto) {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobSearchFilter jobSearchFilter = new JobSearchFilter();

        int totalCount = jobDao.getAllJobs().size();
        if (filterDto.getJobLabelId() != 0) {
            JobLabelModel label = jobLabelDao.getJobLabelModelById(filterDto.getJobLabelId());
            Set<Integer> allLabelIds = KweryUtil.allJobLabelIds(label) ;
            jobSearchFilter.setJobLabelIds(allLabelIds);
            totalCount = jobDao.getJobsByJobLabelIds(allLabelIds).size();
        }

        jobSearchFilter.setPageNo(filterDto.getPageNumber());
        jobSearchFilter.setResultCount(filterDto.getResultCount());

        List<JobModel> jobs = jobDao.filterJobs(jobSearchFilter);

        List<JobModelHackDto> jobModelHackDtos = new ArrayList<>(jobs.size());

        for (JobModel job : jobs) {
            jobModelHackDtos.add(toJobModelHackDto(job));
        }

        JobListDto jobListDto = new JobListDto(totalCount, jobModelHackDtos);

        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(jobListDto);
    }

    @VisibleForTesting
    public JobModelHackDto toJobModelHackDto(JobModel job) {
        JobModelHackDto dto = new JobModelHackDto(job, job.getParentJob());

        if (!"".equals(Strings.nullToEmpty(job.getCronExpression()))) {
            //TODO - Instantiate through Guice
            Predictor predictor = new Predictor(job.getCronExpression());
            dto.setNextExecution(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(predictor.nextMatchingDate()));
        }

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(job.getId());
        filter.setPageNumber(0);
        filter.setResultCount(1);
        List<JobExecutionModel> executions = jobExecutionDao.filter(filter);

        if (!executions.isEmpty()) {
            long start = executions.get(0).getExecutionStart();
            dto.setLastExecution(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(new Date(start)));
        }
        return dto;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listAllJobs() {
        if (logger.isTraceEnabled()) logger.trace("<");
        List<JobModel> jobs = jobDao.getAllJobs();
        List<JobModelHackDto> jobModelHackDtos = jobs.stream().map(jobModel -> new JobModelHackDto(jobModel, jobModel.getParentJob())).collect(toList());
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(jobModelHackDtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listJobExecutions(@PathParam("jobId") int jobId, JobExecutionListFilterDto filterDto, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobId);
        filter.setPageNumber(filterDto.getPageNumber());
        filter.setResultCount(filterDto.getResultCount());

        Result json = json();

        List<String> errorMessages = new ArrayList<>(2);
        //TODO - Move this to validator
        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionStartStart()))) {
            try {
                filter.setExecutionStartStart(new SimpleDateFormat(DISPLAY_DATE_FORMAT).parse(filterDto.getExecutionStartStart()).getTime());
            } catch (ParseException e) {
                errorMessages.add(messages.get(JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR, context, Optional.of(json), filterDto.getExecutionStartStart()).get());
            }
        }

        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionStartEnd()))) {
            try {
                filter.setExecutionStartEnd(new SimpleDateFormat(DISPLAY_DATE_FORMAT).parse(filterDto.getExecutionStartEnd()).getTime());
            } catch (ParseException e) {
                errorMessages.add(messages.get(JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR, context, Optional.of(json), filterDto.getExecutionStartEnd()).get());
            }
        }

        if (filterDto.getExecutionId() != null) {
            filter.setExecutionId(filterDto.getExecutionId());
        }

        Result response = null;

        if (!errorMessages.isEmpty()) {
            ActionResult actionResult = new ActionResult(failure, errorMessages);
            response = json.render(actionResult);
        } else {
            List<JobExecutionModel> executions = jobExecutionDao.filter(filter);

            List<JobExecutionDto> dtos = new ArrayList<>(executions.size());

            for (JobExecutionModel execution : executions) {
                dtos.add(jobExecutionModelToJobExecutionDto(execution));
            }

            JobExecutionListDto dto = new JobExecutionListDto();
            dto.setJobExecutionDtos(dtos);
            dto.setTotalCount(jobExecutionDao.count(filter));
            response = json.render(dto);
        }

        if (logger.isTraceEnabled()) logger.trace(">");
        return response;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result jobExecutionResult(@PathParam("jobExecutionId") String jobExecutionId, Context context) throws IOException {
        if (logger.isTraceEnabled()) logger.trace("<");

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setExecutionId(jobExecutionId);

        List<JobExecutionModel> jobExecutionModels = jobExecutionDao.filter(filter);

        Result json = json();
        JobExecutionModel jobExecutionModel = null;

        if (!jobExecutionModels.isEmpty()) {
            jobExecutionModel = jobExecutionModels.get(0);
        }

        if (jobExecutionModel == null || jobExecutionModel.getSqlQueryExecutionModels().isEmpty()) {
            if (logger.isTraceEnabled()) logger.trace(">");
            String message = messages.get(JOBAPICONTROLLER_REPORT_NOT_FOUND, context, Optional.of(json)).get();
            return json.render(new ActionResult(failure, message));
        } else {
            List<SqlQueryExecutionResultDto> sqlQueryExecutionResultDtos = new LinkedList<>();
            if (!jobExecutionModels.isEmpty()) {
                for (SqlQueryExecutionModel sqlQueryExecutionModel : ReportUtil.orderedExecutions(jobExecutionModel)) {
                    if (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.SUCCESS) {
                        if (isInsertQuery(sqlQueryExecutionModel)) {
                            SqlQueryExecutionResultDto dto = new SqlQueryExecutionResultDto();
                            dto.setTitle(sqlQueryExecutionModel.getSqlQuery().getTitle());
                            dto.setStatus(sqlQueryExecutionModel.getStatus());
                            dto.setErrorResult("");
                            sqlQueryExecutionResultDtos.add(dto);
                        } else {
                            SqlQueryExecutionResultDto dto = new SqlQueryExecutionResultDto();
                            dto.setTitle(sqlQueryExecutionModel.getSqlQuery().getTitle());
                            dto.setStatus(sqlQueryExecutionModel.getStatus());
                            dto.setExecutionId(sqlQueryExecutionModel.getExecutionId());
                            sqlQueryExecutionResultDtos.add(dto);

                            String resultFileName = sqlQueryExecutionModel.getResultFileName();
                            File resultFile = kweryDirectory.getFile(resultFileName);
                            if (logger.isTraceEnabled()) logger.trace("Result file - " + resultFile);

                            if (KweryUtil.isFileWithinLimits(resultFile)) {
                                try (FileReader reader = new FileReader(resultFile);
                                     CSVReader csvReader = csvReaderFactory.create(reader)) {
                                    dto.setJsonResult(csvReader.readAll());
                                }
                            } else {
                                String message = messages.get(MessageKeys.JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING, context, Optional.of(json)).get();
                                dto.setWarning(message);
                            }
                        }
                    } else if (sqlQueryExecutionModel.getStatus() == SqlQueryExecutionModel.Status.FAILURE) {
                        SqlQueryExecutionResultDto dto = new SqlQueryExecutionResultDto();
                        dto.setTitle(sqlQueryExecutionModel.getSqlQuery().getTitle());
                        dto.setStatus(sqlQueryExecutionModel.getStatus());
                        dto.setErrorResult(sqlQueryExecutionModel.getExecutionError());
                        sqlQueryExecutionResultDtos.add(dto);
                    }
                }
            }

            if (logger.isTraceEnabled()) logger.trace(">");
            return json.render(new JobExecutionResultDto(jobExecutionModel.getJobModel().getTitle(), sqlQueryExecutionResultDtos, jobExecutionModel.getStatus()));
        }
    }

    private boolean isInsertQuery(SqlQueryExecutionModel sqlQueryExecutionModel) {
        return sqlQueryExecutionModel.getSqlQuery().getQuery().toLowerCase().startsWith("insert");
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result getJob(@PathParam("jobId") int jobId) {
        if (logger.isTraceEnabled()) logger.trace("<");
        JobModel jobModel = jobDao.getJobById(jobId);
        if (logger.isTraceEnabled()) logger.trace(">");
        return json().render(new JobModelHackDto(jobModel, jobModel.getParentJob()));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result stopJobExecution(@PathParam("jobExecutionId") String executionId) {
        if (logger.isTraceEnabled()) logger.trace("<");
        boolean stopped = jobService.stopExecution(executionId);
        if (logger.isTraceEnabled()) logger.trace(">");

        ActionResult actionResult = null;

        if (stopped) {
            actionResult = new ActionResult(success, "");
        } else {
            actionResult = new ActionResult(failure, "");
        }

        return json().render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result reportAsCsv(@PathParam("sqlQueryExecutionId") String executionId) {
        if (logger.isTraceEnabled()) logger.trace("<");

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionId(executionId);

        List<SqlQueryExecutionModel> models = sqlQueryExecutionDao.filter(filter);

        if (!models.isEmpty()) {
            SqlQueryExecutionModel model = models.get(0);
            String fileName = fileName(model.getSqlQuery().getTitle(), model.getJobExecutionModel().getExecutionStart());
            if (logger.isTraceEnabled()) logger.trace(">");
            return new Result(SC_200_OK).render((context, result) -> {
                result.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                result.contentType("text/csv");
                ResponseStreams responseStreams = context.finalizeHeaders(result);
                try (OutputStream to = responseStreams.getOutputStream();
                     FileInputStream from = new FileInputStream(kweryDirectory.getFile(model.getResultFileName()))) {
                    ByteStreams.copy(from, to);
                } catch (IOException e) {
                    logger.error("Exception while copying csv input stream to output stream for sql query execution id {}", executionId, e);
                }
            });
        } else {
            logger.error("SQL query execution with id {} not found", executionId);
            if (logger.isTraceEnabled()) logger.trace(">");
            return new Result(SC_404_NOT_FOUND);
        }
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result deleteJobExecution(@PathParam("jobExecutionId") Integer jobExecutionModelId) {
        jobExecutionDao.deleteJobExecutions(ImmutableList.of(jobExecutionModelId));
        return json().render(new ActionResult(success, ""));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result searchJobs(SearchFilter searchFilter) {
        List<JobModel> jobs = jobSearchDao.search(searchFilter);
        List<JobModelHackDto> jobModelHackDtos = jobs.stream().map(this::toJobModelHackDto).collect(toList());
        return json().render(jobModelHackDtos);
    }

    @VisibleForTesting
    public JobModel jobDtoToJobModel(JobDto jobDto) {
        JobModel jobModel = new JobModel();

        if (jobDto.getId() == 0) {
            jobModel.setId(null);
        } else {
            jobModel.setId(jobDto.getId());
        }

        jobModel.setName(jobDto.getName());
        jobModel.setCronExpression(Strings.nullToEmpty(jobDto.getCronExpression()));
        jobModel.setSqlQueries(jobDto.getSqlQueries().stream().map(this::sqlQueryDtoToSqlQueryModel).collect(toList()));
        jobModel.setTitle(jobDto.getTitle());
        jobModel.setEmails(jobDto.getEmails());
        jobModel.setFailureAlertEmails(jobDto.getFailureAlertEmails());
        jobModel.setJobRuleModel(jobDto.getJobRuleModel());

        if (jobDto.getLabelIds() != null) {
            jobModel.setLabels(jobDto.getLabelIds().stream().filter(id -> id != null && id > 0).map(jobLabelDao::getJobLabelModelById).collect(toSet()));
        } else {
            jobModel.setLabels(new HashSet<>());
        }

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(jobDto.isEmptyReportNoEmailRule())));

        return jobModel;
    }

    @VisibleForTesting
    public SqlQueryModel sqlQueryDtoToSqlQueryModel(SqlQueryDto dto) {
        SqlQueryModel model = new SqlQueryModel();

        if (dto.getId() == 0) {
            model.setId(null);
        } else {
            model.setId(dto.getId());
        }

        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setDatasource(datasourceDao.getById(dto.getDatasourceId()));
        model.setTitle(dto.getTitle());
        model.setSqlQueryEmailSettingModel(dto.getSqlQueryEmailSetting());

        return model;
    }

    @VisibleForTesting
    public JobExecutionDto jobExecutionModelToJobExecutionDto(JobExecutionModel model) {
        JobExecutionDto jobExecutionDto = new JobExecutionDto();
        jobExecutionDto.setId(model.getId());
        jobExecutionDto.setStart(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionStart()));

        if (model.getStatus() == JobExecutionModel.Status.ONGOING) {
            jobExecutionDto.setEnd("");
        } else {
            jobExecutionDto.setEnd(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionEnd()));
        }

        jobExecutionDto.setStatus(model.getStatus().name());
        jobExecutionDto.setExecutionId(model.getExecutionId());
        jobExecutionDto.setLabel(model.getJobModel().getName());

        return jobExecutionDto;
    }

    @VisibleForTesting
    public boolean isJson(String str) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        long now = System.currentTimeMillis();
        System.out.println("Now - " + now);
        System.out.println("Date - " + new Date(now));
    }
}
