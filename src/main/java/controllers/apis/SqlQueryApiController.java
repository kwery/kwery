package controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import controllers.MessageKeys;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import dtos.SqlQueryDto;
import dtos.SqlQueryExecutionDto;
import dtos.SqlQueryExecutionListDto;
import dtos.SqlQueryExecutionListFilterDto;
import filters.DashRepoSecureFilter;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import services.scheduler.SchedulerService;
import services.scheduler.SqlQueryExecutionNotFoundException;
import services.scheduler.SqlQueryExecutionSearchFilter;
import views.ActionResult;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static controllers.ControllerUtil.fieldMessages;
import static controllers.MessageKeys.DATASOURCE_VALIDATION;
import static controllers.MessageKeys.QUERY_RUN_ADDITION_FAILURE;
import static models.SqlQueryExecution.Status.ONGOING;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public class SqlQueryApiController {
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
        Result json = Results.json();
        ActionResult actionResult = null;

        if (validation.hasViolations()) {
            actionResult = new ActionResult(failure, fieldMessages(validation, context, messages, json));
        } else {
            List<String> errorMessages = new LinkedList<>();

            if (sqlQueryDao.getByLabel(sqlQueryDto.getLabel()) != null) {
                errorMessages.add(messages.get(QUERY_RUN_ADDITION_FAILURE, context, Optional.of(json), sqlQueryDto.getLabel()).get());
            }

            Datasource datasource = datasourceDao.getById(sqlQueryDto.getDatasourceId());
            if (datasource == null) {
                errorMessages.add(messages.get(DATASOURCE_VALIDATION, context, Optional.of(json)).get());
            }

            if (errorMessages.size() > 0) {
                actionResult = new ActionResult(failure, errorMessages);
            } else {
                schedulerService.schedule(sqlQueryDto);
                actionResult = new ActionResult(
                        success,
                        messages.get(MessageKeys.QUERY_RUN_ADDITION_SUCCESS, context, Optional.of(json)).get()
                );
            }
        }

        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueries() {
        List<SqlQuery> sqlQueries = sqlQueryDao.getAll();
        return Results.json().render(sqlQueries);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result executingSqlQueries() {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(ONGOING));
        List<SqlQueryExecution> models = sqlQueryExecutionDao.filter(filter);
        Collections.sort(models, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        List<SqlQueryExecutionDto> dtos = new ArrayList<>(models.size());

        for (SqlQueryExecution model : models) {
            dtos.add(from(model));
        }

        return Results.json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result killSqlQuery(@PathParam("sqlQueryId") Integer sqlQueryId, SqlQueryExecutionIdContainer sqlQueryExecutionId) {
        ActionResult actionResult = new ActionResult(success, new LinkedList<>());

        try {
            schedulerService.stopExecution(sqlQueryId, sqlQueryExecutionId.getSqlQueryExecutionId());
        } catch (SqlQueryExecutionNotFoundException e) {
            actionResult.setStatus(failure);
        }

        return Results.json().render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueryExecution(@PathParam("sqlQueryId") Integer sqlQueryId, SqlQueryExecutionListFilterDto filterDto) throws ParseException {
        SqlQueryExecutionSearchFilter dbFilter = new SqlQueryExecutionSearchFilter();
        dbFilter.setSqlQueryId(sqlQueryId);

        dbFilter.setPageNumber(filterDto.getPageNumber());
        dbFilter.setResultCount(filterDto.getResultCount());

        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionStartStart()))) {
            dbFilter.setExecutionStartStart(getTime(filterDto.getExecutionStartStart()));
        }

        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionStartEnd()))) {
            dbFilter.setExecutionStartEnd(getTime(filterDto.getExecutionStartEnd()));
        }

        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionEndStart()))) {
            dbFilter.setExecutionEndStart(getTime(filterDto.getExecutionEndStart()));
        }

        if (!"".equals(Strings.nullToEmpty(filterDto.getExecutionEndEnd()))) {
            dbFilter.setExecutionEndEnd(getTime(filterDto.getExecutionEndEnd()));
        }

        if (filterDto.getStatuses() != null && filterDto.getStatuses().size() > 0) {
            List<SqlQueryExecution.Status> fromRequest = new ArrayList<>(filterDto.getStatuses().size());
            for (String status : filterDto.getStatuses()) {
                fromRequest.add(SqlQueryExecution.Status.valueOf(status));
            }

            dbFilter.setStatuses(fromRequest);
        }

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(dbFilter);

        Collections.sort(sqlQueryExecutions, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));

        List<SqlQueryExecutionDto> sqlQueryExecutionDtos = new ArrayList<>(sqlQueryExecutions.size());

        for (SqlQueryExecution sqlQueryExecution : sqlQueryExecutions) {
            sqlQueryExecutionDtos.add(from(sqlQueryExecution));
        }

        SqlQuery sqlQuery = sqlQueryDao.getById(sqlQueryId);

        SqlQueryExecutionListDto sqlQueryExecutionListDto = new SqlQueryExecutionListDto();
        sqlQueryExecutionListDto.setSqlQuery(sqlQuery.getQuery());
        sqlQueryExecutionListDto.setSqlQueryExecutionDtos(sqlQueryExecutionDtos);
        sqlQueryExecutionListDto.setTotalCount(sqlQueryExecutionDao.count(dbFilter));

        return Results.json().render(sqlQueryExecutionListDto);
    }

    public Result sqlQueryExecutionResult(@PathParam("sqlQueryId") Integer sqlQueryId, @PathParam("sqlQueryExecutionId") String sqlQueryExecutionId) throws IOException {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);
        filter.setExecutionId(sqlQueryExecutionId);

        List<List<?>> jsonResult = null;

        List<SqlQueryExecution> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

        if (sqlQueryExecutions.size() == 0) {
            jsonResult = new LinkedList<>(new LinkedList<>());
        } else {
            SqlQueryExecution sqlQueryExecution = sqlQueryExecutions.get(0);

            //TODO - Generic type
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResult = objectMapper.readValue(
                    sqlQueryExecution.getResult(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, List.class)
            );
        }

        return Results.json().render(jsonResult);
    }

    public SqlQueryExecutionDto from(SqlQueryExecution model) {
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
}
