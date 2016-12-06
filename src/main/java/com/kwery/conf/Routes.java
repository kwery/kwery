package com.kwery.conf;

import com.kwery.controllers.IndexController;
import com.kwery.controllers.apis.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";

    public static final String ADD_ADMIN_USER_API = "/api/user/add-admin-user";
    public static final String ADD_DATASOURCE_API = "/api/datasource/add-datasource";
    public static final String MYSQL_DATASOURCE_CONNECTION_TEST_API = "/api/datasource/test-connection";
    public static final String LOGIN_API = "/api/user/login";
    public static final String LOGOUT_API = "/api/user/logout";
    public static final String USER = "/api/user";
    public static final String USER_API = "/api/user/{userId}";
    public static final String DELETE_USER_API = "/api/user/delete/{userId}";
    public static final String ADD_SQL_QUERY_API = "/api/sql-query/add";
    public static final String ALL_DATASOURCES_API = "/api/datasource/all";
    public static final String EXECUTING_SQL_QUERY_API = "/api/sql-query/executing";
    public static final String KILL_SQL_QUERY_API = "/api/sql-query/kill/{sqlQueryId}";
    public static final String LIST_SQL_QUERY_EXECUTION_API = "/api/sql-query/{sqlQueryId}/execution";
    public static final String LIST_LATEST_SQL_QUERY_EXECUTIONS_API = "/api/sql-query/latest-execution";
    public static final String SQL_QUERY_API = "/api/sql-query/{sqlQueryId}";
    public static final String SQL_QUERY_EXECUTION_RESULT_API = "/api/sql-query/{sqlQueryId}/execution/{sqlQueryExecutionId}";
    public static final String DELETE_SQL_QUERY_API = "/api/sql-query/delete/{sqlQueryId}";
    public static final String LIST_SQL_QUERIES_API = "/api/sql-query/list";
    public static final String LIST_USERS_API = "/api/user/list";
    public static final String DATASOURCE_API = "/api/datasource/{datasourceId}";
    public static final String DELETE_DATASOURCE_API = "/api/datasource/delete/{datasourceId}";
    public static final String ONBOARDING_ADD_ROOT_USER_API = "/api/onboarding/user/add";
    public static final String ONBOARDING_NEXT_ACTION_API = "/api/onboarding/next-action";
    public static final String SQL_QUERY_ONE_OFF_EXECUTION_API = "/api/sql-query/one-off-execution/{sqlQueryId}";

    public static final String MAIL_SAVE_SMTP_CONFIGURATION_API = "/api/mail/save-smtp-configuration";
    public static final String MAIL_GET_SMTP_CONFIGURATION_API = "/api/mail/smtp-configuration";
    public static final String MAIL_SAVE_EMAIL_CONFIGURATION_API = "/api/mail/save-email-configuration";
    public static final String MAIL_GET_EMAIL_CONFIGURATION_API = "/api/mail/email-configuration";
    public static final String MAIL_CONFIGURATION_TEST_API = "/api/mail/{toEmail}/email-configuration-test";

    public static final String JOB_SAVE_API = "/api/job/save";
    public static final String JOB_LIST_API = "/api/job/list";

    @Override
    public void init(Router router) {
        router.GET().route(INDEX).with(IndexController.class, "index");

        //Api - Start
        router.POST().route(ADD_ADMIN_USER_API).with(UserApiController.class, "addAdminUser");
        router.POST().route(LOGIN_API).with(UserApiController.class, "login");
        router.POST().route(LOGOUT_API).with(UserApiController.class, "logout");
        router.GET().route(LIST_USERS_API).with(UserApiController.class, "list");
        router.GET().route(USER).with(UserApiController.class, "user");
        router.GET().route(USER_API).with(UserApiController.class, "userById");
        router.POST().route(DELETE_USER_API).with(UserApiController.class, "delete");

        router.POST().route(ADD_DATASOURCE_API).with(DatasourceApiController.class, "addDatasource");
        router.POST().route(MYSQL_DATASOURCE_CONNECTION_TEST_API).with(DatasourceApiController.class, "testConnection");
        router.GET().route(ALL_DATASOURCES_API).with(DatasourceApiController.class, "allDatasources");
        router.GET().route(DATASOURCE_API).with(DatasourceApiController.class, "datasource");
        router.POST().route(DELETE_DATASOURCE_API).with(DatasourceApiController.class, "delete");

        router.POST().route(ADD_SQL_QUERY_API).with(SqlQueryApiController.class, "addSqlQuery");
        router.GET().route(EXECUTING_SQL_QUERY_API).with(SqlQueryApiController.class, "executingSqlQueries");
        router.POST().route(KILL_SQL_QUERY_API).with(SqlQueryApiController.class, "killSqlQuery");
        router.POST().route(LIST_SQL_QUERY_EXECUTION_API).with(SqlQueryApiController.class, "listSqlQueryExecution");
        router.GET().route(SQL_QUERY_EXECUTION_RESULT_API).with(SqlQueryApiController.class, "sqlQueryExecutionResult");
        router.GET().route(LIST_SQL_QUERIES_API).with(SqlQueryApiController.class, "listSqlQueries");
        router.GET().route(LIST_LATEST_SQL_QUERY_EXECUTIONS_API).with(SqlQueryApiController.class, "latestSqlQueryExecutions");
        router.GET().route(SQL_QUERY_API).with(SqlQueryApiController.class, "sqlQuery");
        router.POST().route(DELETE_SQL_QUERY_API).with(SqlQueryApiController.class, "delete");
        router.POST().route(SQL_QUERY_ONE_OFF_EXECUTION_API).with(SqlQueryApiController.class, "oneOffSqlQueryExecution");

        router.POST().route(ONBOARDING_ADD_ROOT_USER_API).with(OnboardingApiController.class, "addRootUser");
        router.GET().route(ONBOARDING_NEXT_ACTION_API).with(OnboardingApiController.class, "nextAction");

        router.POST().route(MAIL_SAVE_SMTP_CONFIGURATION_API).with(MailApiController.class, "saveSmtpConfiguration");
        router.GET().route(MAIL_GET_SMTP_CONFIGURATION_API).with(MailApiController.class, "getSmtpConfiguration");
        router.POST().route(MAIL_SAVE_EMAIL_CONFIGURATION_API).with(MailApiController.class, "saveEmailConfiguration");
        router.GET().route(MAIL_GET_EMAIL_CONFIGURATION_API).with(MailApiController.class, "getEmailConfiguration");
        router.POST().route(MAIL_SAVE_EMAIL_CONFIGURATION_API).with(MailApiController.class, "saveEmailConfiguration");
        router.POST().route(MAIL_CONFIGURATION_TEST_API).with(MailApiController.class, "testEmailConfiguration");

        router.POST().route(JOB_SAVE_API).with(JobApiController.class, "saveJob");
        router.GET().route(JOB_LIST_API).with(JobApiController.class, "listAllJobs");
        //Api - End

        //Static asset
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    }
}
