package com.kwery.controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.dtos.SqlQueryExecutionDto;
import com.kwery.dtos.SqlQueryExecutionListDto;
import com.kwery.dtos.SqlQueryExecutionListFilterDto;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.services.scheduler.SqlQueryExecutionNotFoundException;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.views.ActionResult;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.validation.FieldViolation;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static com.kwery.controllers.ControllerUtil.fieldMessages;
import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

public class SqlQueryApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DISPLAY_DATE_FORMAT = "EEE MMM dd yyyy HH:mm";
    public static final String FILTER_DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @Inject
    private SqlQueryDao sqlQueryDao;

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    private Messages messages;

    @Inject
    private SchedulerService schedulerService;

    @Inject
    private SqlQueryExecutionDao sqlQueryExecutionDao;

    @FilterWith(DashRepoSecureFilter.class)
    public Result addSqlQuery(@JSR303Validation SqlQueryDto sqlQueryDto, Context context, Validation validation) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Sql query with payload - " + sqlQueryDto);

        Result json = json();
        ActionResult actionResult = null;

        boolean isUpdate = false;

        if (sqlQueryDto.getId() != 0) {
            isUpdate = true;
        }

        if (isUpdate) {
            logger.info("Updating sql query with payload - " + sqlQueryDto);
        } else {
            logger.info("Adding sql query with payload - " + sqlQueryDto);
        }

        if (validation.hasViolations()) {
            List<String> violations = new ArrayList<>();
            violations.add("Validation errors:");

            for (FieldViolation fieldViolation : validation.getBeanViolations()) {
                violations.add(fieldViolation.constraintViolation.getMessageKey());
            }

            for (FieldViolation fieldViolation : validation.getFieldViolations()) {
                violations.add(fieldViolation.constraintViolation.getMessageKey());
            }

            logger.error(Joiner.on(System.getProperty("line.separator")).join(violations));

            actionResult = new ActionResult(failure, fieldMessages(validation, context, messages, json));
        } else {
            List<String> errorMessages = new LinkedList<>();

            SqlQueryModel fromDb = sqlQueryDao.getByLabel(sqlQueryDto.getLabel());

            if (isUpdate) {
                if (fromDb != null && !fromDb.getId().equals(sqlQueryDto.getId())) {
                    logger.error("{} SQL query already exists with label {}", fromDb.getId(), sqlQueryDto.getLabel());
                    errorMessages.add(messages.get(QUERY_RUN_ADDITION_FAILURE, context, Optional.of(json), sqlQueryDto.getLabel()).get());
                }
            } else {
                if (fromDb != null) {
                    logger.error("{} SQL query already exists with label {}", fromDb.getId(), sqlQueryDto.getLabel());
                    errorMessages.add(messages.get(QUERY_RUN_ADDITION_FAILURE, context, Optional.of(json), sqlQueryDto.getLabel()).get());
                }
            }

            Datasource datasource = datasourceDao.getById(sqlQueryDto.getDatasourceId());
            if (datasource == null) {
                logger.error("Cannot add SQL query without datasource");
                errorMessages.add(messages.get(DATASOURCE_VALIDATION, context, Optional.of(json)).get());
            }

            if (errorMessages.size() > 0) {
                actionResult = new ActionResult(failure, errorMessages);
            } else {
                SqlQueryModel model = toSqlQueryModel(sqlQueryDto);

                if (isUpdate) {
                    //Stop existing schedules
                    logger.info("Stopping existing scheduler");
                    schedulerService.stopScheduler(sqlQueryDto.getId());
                    sqlQueryDao.save(model);
                    actionResult = new ActionResult(
                            success,
                            messages.get(QUERY_RUN_UPDATE_SUCCESS, context, Optional.of(json)).get()
                    );
                    schedulerService.schedule(model);
                } else {
                    sqlQueryDao.save(model);

                    if (sqlQueryDto.getDependsOnSqlQueryId() != null && sqlQueryDto.getDependsOnSqlQueryId() > 0) {
                        SqlQueryModel dependsOnSqlQuery = sqlQueryDao.getById(sqlQueryDto.getDependsOnSqlQueryId());
                        dependsOnSqlQuery.getDependentQueries().add(model);
                        sqlQueryDao.save(dependsOnSqlQuery);
                    }

                    if (isOneOffSqlQuery(sqlQueryDto)) {
                        actionResult = new ActionResult(
                                success,
                                messages.get(QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS, context, Optional.of(json)).get()
                        );
                    } else {
                        actionResult = new ActionResult(
                                success,
                                messages.get(QUERY_RUN_WITH_CRON_ADDITION_SUCCESS, context, Optional.of(json)).get()
                        );
                        schedulerService.schedule(model);
                    }
                }
            }
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(actionResult);
    }

    protected boolean isOneOffSqlQuery(SqlQueryDto sqlQueryDto) {
        return "".equals(nullToEmpty(sqlQueryDto.getCronExpression()));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueries() {
        if (logger.isTraceEnabled()) logger.trace(">");
        List<SqlQueryModel> sqlQueries = sqlQueryDao.getAll();
        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(sqlQueries);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result executingSqlQueries() {
        if (logger.isTraceEnabled()) logger.trace(">");
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(ONGOING));
        List<SqlQueryExecutionModel> models = sqlQueryExecutionDao.filter(filter);
        Collections.sort(models, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        List<SqlQueryExecutionDto> dtos = new ArrayList<>(models.size());

        for (SqlQueryExecutionModel model : models) {
            dtos.add(from(model));
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result killSqlQuery(@PathParam("sqlQueryId") Integer sqlQueryId, SqlQueryExecutionIdContainer sqlQueryExecutionId) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Killing SQL query with id {}", sqlQueryId);

        ActionResult actionResult = new ActionResult(success, new LinkedList<>());

        try {
            schedulerService.stopExecution(sqlQueryId, sqlQueryExecutionId.getSqlQueryExecutionId());
        } catch (SqlQueryExecutionNotFoundException e) {
            logger.error("Exception while killing sql query with id {}", sqlQueryId, e);
            actionResult.setStatus(failure);
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueryExecution(@PathParam("sqlQueryId") Integer sqlQueryId, SqlQueryExecutionListFilterDto filterDto) throws ParseException {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryExecutionSearchFilter dbFilter = new SqlQueryExecutionSearchFilter();
        dbFilter.setSqlQueryId(sqlQueryId);

        dbFilter.setPageNumber(filterDto.getPageNumber());
        dbFilter.setResultCount(filterDto.getResultCount());

        if (!"".equals(nullToEmpty(filterDto.getExecutionStartStart()))) {
            dbFilter.setExecutionStartStart(getTime(filterDto.getExecutionStartStart()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionStartEnd()))) {
            dbFilter.setExecutionStartEnd(getTime(filterDto.getExecutionStartEnd()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionEndStart()))) {
            dbFilter.setExecutionEndStart(getTime(filterDto.getExecutionEndStart()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionEndEnd()))) {
            dbFilter.setExecutionEndEnd(getTime(filterDto.getExecutionEndEnd()));
        }

        if (filterDto.getStatuses() != null && filterDto.getStatuses().size() > 0) {
            List<SqlQueryExecutionModel.Status> fromRequest = new ArrayList<>(filterDto.getStatuses().size());
            for (String status : filterDto.getStatuses()) {
                fromRequest.add(SqlQueryExecutionModel.Status.valueOf(status));
            }

            dbFilter.setStatuses(fromRequest);
        }

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(dbFilter);

        Collections.sort(sqlQueryExecutions, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        List<SqlQueryExecutionDto> sqlQueryExecutionDtos = new ArrayList<>(sqlQueryExecutions.size());

        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
            sqlQueryExecutionDtos.add(from(sqlQueryExecution));
        }

        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryId);

        SqlQueryExecutionListDto sqlQueryExecutionListDto = new SqlQueryExecutionListDto();
        sqlQueryExecutionListDto.setSqlQuery(sqlQuery.getQuery());
        sqlQueryExecutionListDto.setSqlQueryExecutionDtos(sqlQueryExecutionDtos);
        sqlQueryExecutionListDto.setTotalCount(sqlQueryExecutionDao.count(dbFilter));

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(sqlQueryExecutionListDto);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result sqlQueryExecutionResult(@PathParam("sqlQueryId") Integer sqlQueryId, @PathParam("sqlQueryExecutionId") String sqlQueryExecutionId) throws IOException {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);
        filter.setExecutionId(sqlQueryExecutionId);

        List<List<?>> jsonResult = null;

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

        if (sqlQueryExecutions.size() == 0) {
            jsonResult = new LinkedList<>(new LinkedList<>());
        } else {
            SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutions.get(0);

            //TODO - Generic type
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResult = objectMapper.readValue(
                    sqlQueryExecution.getResult(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, List.class)
            );
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(jsonResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result sqlQuery(@PathParam("sqlQueryId") int id) {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryModel sqlQuery = sqlQueryDao.getById(id);

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(sqlQuery);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result delete(@PathParam("sqlQueryId") int sqlQueryId, Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Deleting SQL query - " + sqlQueryId);

        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryId);
        //TODO - This should be part of service layer
        sqlQueryDao.delete(sqlQueryId);
        schedulerService.stopScheduler(sqlQueryId);
        Result json = json();
        String message = messages.get(SQL_QUERY_DELETE_SUCCESS, context, Optional.of(json), sqlQuery.getLabel()).get();

        if (logger.isTraceEnabled()) logger.trace("<");

        return json.render(new ActionResult(success, message));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result latestSqlQueryExecutions() {
        if (logger.isTraceEnabled()) logger.trace(">");

        List<SqlQueryModel> sqlQueries = sqlQueryDao.getAll();

        List<Integer> sqlQueryIds = new ArrayList<>(sqlQueries.size());

        for (SqlQueryModel sqlQuery : sqlQueries) {
            sqlQueryIds.add(sqlQuery.getId());
        }

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.lastSuccessfulExecution(sqlQueryIds);

        List<SqlQueryExecutionDto> dtos = from(sqlQueryExecutions);

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(dtos);
    }

    //TODO - Error handling
    @FilterWith(DashRepoSecureFilter.class)
    public Result oneOffSqlQueryExecution(@PathParam("sqlQueryId") int sqlQueryId, Context context) {
        if (logger.isTraceEnabled()) logger.trace("<");

        logger.info("One off execution of query - " + sqlQueryId);

        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryId);
        sqlQuery.setCronExpression("");

        schedulerService.schedule(sqlQuery);

        Result json = json();
        String message = messages.get(ONE_OFF_EXECUTION_SUCCESS_MESSAGE, context, Optional.of(json), sqlQuery.getLabel()).get();

        if (logger.isTraceEnabled()) logger.trace(">");

        return json().render(new ActionResult(ActionResult.Status.success, message));
    }

    public SqlQueryExecutionDto from(SqlQueryExecutionModel model) {
        SqlQueryExecutionDto dto = new SqlQueryExecutionDto();
        dto.setSqlQueryLabel(model.getSqlQuery().getLabel());
        dto.setDatasourceLabel(model.getSqlQuery().getDatasource().getLabel());
        dto.setSqlQueryExecutionStartTime(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionStart()));

        Long executionEnd = model.getExecutionEnd();
        if (executionEnd != null) {
            dto.setSqlQueryExecutionEndTime(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(executionEnd));
        }

        dto.setSqlQueryId(model.getSqlQuery().getId());
        dto.setSqlQueryExecutionId(model.getExecutionId());
        dto.setStatus(model.getStatus().name());
        dto.setResult(model.getResult());

        return dto;
    }

    public List<SqlQueryExecutionDto> from(List<SqlQueryExecutionModel> models) {
        List<SqlQueryExecutionDto> dtos = new ArrayList<>(models.size());

        for (SqlQueryExecutionModel model : models) {
            dtos.add(from(model));
        }

        return dtos;
    }

    private long getTime(String date) throws ParseException {
        return new SimpleDateFormat(FILTER_DATE_FORMAT).parse(date).getTime();
    }

    public static class SqlQueryExecutionIdContainer {
        private String sqlQueryExecutionId;

        public String getSqlQueryExecutionId() {
            return sqlQueryExecutionId;
        }

        public void setSqlQueryExecutionId(String sqlQueryExecutionId) {
            this.sqlQueryExecutionId = sqlQueryExecutionId;
        }
    }

    public SqlQueryModel toSqlQueryModel(SqlQueryDto dto) {
        SqlQueryModel model = new SqlQueryModel();

        if (dto.getId() == 0) {
            model.setId(null);
        } else {
            model.setId(dto.getId());
        }

        model.setCronExpression(nullToEmpty(dto.getCronExpression()).trim());
        model.setLabel(dto.getLabel());
        model.setQuery(dto.getQuery());
        model.setDatasource(datasourceDao.getById(dto.getDatasourceId()));

        if (!"".equals(Strings.nullToEmpty(dto.getRecipientEmailsCsv()))) {
            Iterable<String> emails = Splitter.on(",").trimResults().omitEmptyStrings().split(dto.getRecipientEmailsCsv());
            model.setRecipientEmails(newHashSet(emails));
        }

        return model;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }
}
