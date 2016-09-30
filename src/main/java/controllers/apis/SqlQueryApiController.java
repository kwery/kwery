package controllers.apis;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import controllers.MessageKeys;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import dao.SqlQueryExecutionDao;
import dtos.SqlQueryDto;
import filters.DashRepoSecureFilter;
import models.Datasource;
import models.SqlQueryExecution;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import services.scheduler.SchedulerService;
import views.ActionResult;

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
    public Result currentlyExecutingSqlQueries() {
        List<SqlQueryExecution> executions = sqlQueryExecutionDao.getByStatus(ONGOING);
        Collections.sort(executions, (o1, o2) -> o1.getExecutionStart().compareTo(o2.getExecutionStart()));
        return Results.json().render(executions);
    }
}
