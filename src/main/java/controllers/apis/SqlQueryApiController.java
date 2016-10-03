package controllers.apis;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import controllers.MessageKeys;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import dtos.SqlQueryDto;
import dtos.SqlQueryExecutionDto;
import filters.DashRepoSecureFilter;
import models.Datasource;
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
import views.ActionResult;

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
    public Result executingSqlQueries() {
        List<SqlQueryExecution> models = sqlQueryExecutionDao.getByStatus(ONGOING);
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

    public SqlQueryExecutionDto from(SqlQueryExecution model) {
        SqlQueryExecutionDto dto = new SqlQueryExecutionDto();
        dto.setSqlQueryLabel(model.getSqlQuery().getLabel());
        dto.setDatasourceLabel(model.getSqlQuery().getDatasource().getLabel());
        dto.setSqlQueryExecutionStartTime(new SimpleDateFormat("EEE MMM dd yyyy HH:mm").format(model.getExecutionStart()));
        dto.setSqlQueryId(model.getSqlQuery().getId());
        dto.setSqlQueryExecutionId(model.getExecutionId());
        return dto;
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