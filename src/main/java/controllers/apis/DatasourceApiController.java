package controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import filters.DashRepoSecureFilter;
import models.Datasource;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.validation.FieldViolation;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import views.ActionResult;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Optional.of;
import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static models.Datasource.Type.MYSQL;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class DatasourceApiController {
    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    private Messages messages;

    @FilterWith(DashRepoSecureFilter.class)
    public Result addDatasource(
            @JSR303Validation Datasource datasource,
            Context context,
            Validation validation) {
        Result json = Results.json();
        ActionResult actionResult = null;

        if (validation.hasViolations()) {
            List<String> validationMessages = new LinkedList<>();
            for (FieldViolation fieldViolation : validation.getBeanViolations()) {
                String messageKey = fieldViolation.constraintViolation.getMessageKey();
                String message = messages.get(messageKey, context, of(json)).get();
                validationMessages.add(message);
            }
            actionResult = new ActionResult(failure, validationMessages);
        } else {
            Datasource existingDatasource = datasourceDao.getByLabel(datasource.getLabel());
            if (existingDatasource == null) {
                datasourceDao.save(datasource);
                String msg = messages.get(DATASOURCE_ADDITION_SUCCESS, context, of(json), MYSQL.name(), datasource.getLabel()).get();
                actionResult = new ActionResult(success, msg);
            } else {
                String msg = messages.get(DATASOURCE_ADDITION_FAILURE, context, of(json), MYSQL.name(), datasource.getLabel()).get();
                actionResult = new ActionResult(failure, msg);
            }
        }

        return json.render(actionResult);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }
}
