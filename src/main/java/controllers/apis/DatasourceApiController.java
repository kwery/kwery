package controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import controllers.ControllerUtil;
import dao.DatasourceDao;
import filters.DashRepoSecureFilter;
import models.Datasource;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import services.datasource.MysqlDatasourceService;
import views.ActionResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;
import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_FAILURE;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_SUCCESS;
import static models.Datasource.Type.MYSQL;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class DatasourceApiController {
    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    private Messages messages;

    @Inject
    private MysqlDatasourceService mysqlDatasourceService;

    @FilterWith(DashRepoSecureFilter.class)
    public Result addDatasource(
            @JSR303Validation Datasource datasource,
            Context context,
            Validation validation) {
        Result json = Results.json();
        ActionResult actionResult = null;

        Map<String, List<String>> fieldMessages = new HashMap<>();
        if (validation.hasViolations()) {
            fieldMessages = ControllerUtil.fieldMessages(validation, context, messages, json);
        }

        List<String> errorMessages = new LinkedList<>();

        if (datasourceDao.getByLabel(datasource.getLabel()) != null) {
            errorMessages.add(messages.get(DATASOURCE_ADDITION_FAILURE, context, of(json), MYSQL.name(), datasource.getLabel()).get());
        }

        if (!mysqlDatasourceService.testConnection(datasource)) {
            errorMessages.add(messages.get(MYSQL_DATASOURCE_CONNECTION_FAILURE, context, of(json)).get());
        }

        if (fieldMessages.size() > 0 || errorMessages.size() > 0) {
            actionResult = new ActionResult(failure, errorMessages, fieldMessages);
        } else {
            datasourceDao.save(datasource);
            String msg = messages.get(DATASOURCE_ADDITION_SUCCESS, context, of(json), MYSQL.name(), datasource.getLabel()).get();
            actionResult = new ActionResult(success, msg);
        }

        return json.render(actionResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result testConnection(Datasource datasource, Context context) {
        Result json = Results.json();
        ActionResult result;

        if (mysqlDatasourceService.testConnection(datasource)) {
            result = new ActionResult(
                    success,
                    messages.get(MYSQL_DATASOURCE_CONNECTION_SUCCESS, context, of(json)).get()
            );
        } else {
            result = new ActionResult(
                    failure,
                    messages.get(MYSQL_DATASOURCE_CONNECTION_FAILURE, context, of(json)).get()
            );
        }

        return json.render(result);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }

    public void setMysqlDatasourceService(MysqlDatasourceService mysqlDatasourceService) {
        this.mysqlDatasourceService = mysqlDatasourceService;
    }
}
