package com.kwery.controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.views.ActionResult;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;
import static com.kwery.controllers.ControllerUtil.fieldMessages;
import static com.kwery.controllers.MessageKeys.*;
import static com.kwery.views.ActionResult.Status.failure;
import static com.kwery.views.ActionResult.Status.success;
import static ninja.Results.json;

@Singleton
public class DatasourceApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    private Messages messages;

    @Inject
    private DatasourceService datasourceService;

    @Inject
    private SqlQueryDao sqlQueryDao;

    @FilterWith(DashRepoSecureFilter.class)
    public Result addDatasource(@JSR303Validation Datasource datasource, Context context, Validation validation) {
        if (logger.isTraceEnabled()) logger.trace(">");

        Result json = json();
        ActionResult actionResult = null;

        boolean isUpdate = isUpdate(datasource);

        if (isUpdate) {
            logger.info("Datasource payload for update - " + datasource);
        } else {
            logger.info("Datasource payload for addition - " + datasource);
        }

        Map<String, List<String>> fieldMessages = new HashMap<>();
        if (validation.hasViolations()) {
            fieldMessages = fieldMessages(validation, context, messages, json);
            actionResult = new ActionResult(failure, fieldMessages);
        } else {
            List<String> errorMessages = new LinkedList<>();

            Datasource fromDb = datasourceDao.getByLabel(datasource.getLabel());
            if (isUpdate) {
                if (fromDb != null && !datasource.getId().equals(fromDb.getId())) {
                    logger.error("Could not update datasource, a datasource with label {} already exists", datasource.getLabel());
                    errorMessages.add(messages.get(DATASOURCE_UPDATE_FAILURE, context, of(json), datasource.getType().name(), datasource.getLabel()).get());
                }
            } else {
                if (fromDb != null) {
                    logger.error("Could not add datasource, a datasource with label {} already exists", datasource.getLabel());
                    errorMessages.add(messages.get(DATASOURCE_ADDITION_FAILURE, context, of(json), datasource.getType().name(), datasource.getLabel()).get());
                }
            }

            if (!datasourceService.testConnection(datasource)) {
                logger.error("Could not connect to datasource {}", datasource);
                errorMessages.add(messages.get(DATASOURCE_CONNECTION_FAILURE, context, of(json), datasource.getType().name()).get());
            }

            if (errorMessages.size() > 0) {
                actionResult = new ActionResult(failure, errorMessages);
            } else {
                if (isUpdate) {
                    datasourceDao.update(datasource);
                } else {
                    datasourceDao.save(datasource);
                }

                String msg = "";

                if (isUpdate) {
                    msg = messages.get(DATASOURCE_UPDATE_SUCCESS, context, of(json), datasource.getType().name(), datasource.getLabel()).get();
                } else {
                    msg = messages.get(DATASOURCE_ADDITION_SUCCESS, context, of(json), datasource.getType().name(), datasource.getLabel()).get();
                }

                actionResult = new ActionResult(success, msg);
            }
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(actionResult);
    }

    public boolean isUpdate(Datasource datasource) {
        return datasource.getId() != null && datasource.getId() > 0;
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result testConnection(Datasource datasource, Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");

        logger.info("Testing connection to datasource - " + datasource);

        Result json = json();
        ActionResult result;

        if (datasourceService.testConnection(datasource)) {
            logger.info("Successfully connected to datasource");
            result = new ActionResult(
                    success,
                    messages.get(DATASOURCE_CONNECTION_SUCCESS, context, of(json), datasource.getType()).get()
            );
        } else {
            logger.error("Could not connect to datasource");
            result = new ActionResult(
                    failure,
                    messages.get(DATASOURCE_CONNECTION_FAILURE, context, of(json)).get()
            );
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(result);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result allDatasources() {
        if (logger.isTraceEnabled()) logger.trace(">");
        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(datasourceDao.getAll());
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result datasource(@PathParam("datasourceId") int datasourceId) {
        if (logger.isTraceEnabled()) logger.trace(">");
        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(datasourceDao.getById(datasourceId));
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result delete(@PathParam("datasourceId") int datasourceId, Context context) {
        if (logger.isTraceEnabled()) logger.trace(">");
        logger.info("Deleting datasource - " + datasourceId);

        Result json = json();
        ActionResult actionResult = null;
        if (sqlQueryDao.countSqlQueriesWithDatasourceId(datasourceId) > 0) {
            String message = messages.get(DATASOURCE_DELETE_SQL_QUERIES_PRESENT, context, of(json)).get();
            logger.error("Cannot delete datasource {} as scheduled queries are using this datasource", datasourceId);
            actionResult = new ActionResult(failure, message);
        } else {
            Datasource datasource = datasourceDao.getById(datasourceId);
            datasourceDao.delete(datasourceId);
            String message = messages.get(DATASOURCE_DELETE_SUCCESS, context, of(json), datasource.getLabel()).get();
            actionResult = new ActionResult(success, message);
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json.render(actionResult);
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }

    public void setDatasourceService(DatasourceService datasourceService) {
        this.datasourceService = datasourceService;
    }
}
