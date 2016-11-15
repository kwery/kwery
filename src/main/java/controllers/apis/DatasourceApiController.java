package controllers.apis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import filters.DashRepoSecureFilter;
import models.Datasource;
import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.params.PathParam;
import ninja.validation.JSR303Validation;
import ninja.validation.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.datasource.MysqlDatasourceService;
import views.ActionResult;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;
import static controllers.ControllerUtil.fieldMessages;
import static controllers.MessageKeys.DATASOURCE_ADDITION_FAILURE;
import static controllers.MessageKeys.DATASOURCE_ADDITION_SUCCESS;
import static controllers.MessageKeys.DATASOURCE_DELETE_SQL_QUERIES_PRESENT;
import static controllers.MessageKeys.DATASOURCE_DELETE_SUCCESS;
import static controllers.MessageKeys.DATASOURCE_UPDATE_FAILURE;
import static controllers.MessageKeys.DATASOURCE_UPDATE_SUCCESS;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_FAILURE;
import static controllers.MessageKeys.MYSQL_DATASOURCE_CONNECTION_SUCCESS;
import static models.Datasource.Type.MYSQL;
import static ninja.Results.json;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@Singleton
public class DatasourceApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    private Messages messages;

    @Inject
    private MysqlDatasourceService mysqlDatasourceService;

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
                    errorMessages.add(messages.get(DATASOURCE_UPDATE_FAILURE, context, of(json), MYSQL.name(), datasource.getLabel()).get());
                }
            } else {
                if (fromDb != null) {
                    logger.error("Could not add datasource, a datasource with label {} already exists", datasource.getLabel());
                    errorMessages.add(messages.get(DATASOURCE_ADDITION_FAILURE, context, of(json), MYSQL.name(), datasource.getLabel()).get());
                }
            }

            if (!mysqlDatasourceService.testConnection(datasource)) {
                logger.error("Could not add datasource as the test connection to the datasource failed");
                errorMessages.add(messages.get(MYSQL_DATASOURCE_CONNECTION_FAILURE, context, of(json)).get());
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
                    msg = messages.get(DATASOURCE_UPDATE_SUCCESS, context, of(json), MYSQL.name(), datasource.getLabel()).get();
                } else {
                    msg = messages.get(DATASOURCE_ADDITION_SUCCESS, context, of(json), MYSQL.name(), datasource.getLabel()).get();
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

        if (mysqlDatasourceService.testConnection(datasource)) {
            logger.info("Successfully connected to datasource");
            result = new ActionResult(
                    success,
                    messages.get(MYSQL_DATASOURCE_CONNECTION_SUCCESS, context, of(json)).get()
            );
        } else {
            logger.error("Could not connect to datasource");
            result = new ActionResult(
                    failure,
                    messages.get(MYSQL_DATASOURCE_CONNECTION_FAILURE, context, of(json)).get()
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

    public void setMysqlDatasourceService(MysqlDatasourceService mysqlDatasourceService) {
        this.mysqlDatasourceService = mysqlDatasourceService;
    }
}
