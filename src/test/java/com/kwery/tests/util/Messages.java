package com.kwery.tests.util;

/**
 * Holds messages present in messages.properties file, this is done to ease testing. All messages present here
 * should have an equivalent entry in messages.properties file.
 *
 * Convention for key name is to convert the corresponding key in messages.properties to upper case, replace dots
 * with hyphens and append _M. Append _HTML_M if the message is HTML.
 */
public class Messages {
    public static final String TITLE_M = "RepoDash - Create reports, dashboards and visualizations from datasources in a jiffy";
    public static final String INSTALLATION_WELCOME_M = "Welcome to RepoDash, determining the steps for onboarding.";
    public static final String CREATE_ADMIN_USER_M = "Create Admin User";
    public static final String USER_NAME_M = "User Name";
    public static final String PASSWORD_M = "Password";
    public static final String CREATE_M = "create";
    public static final String ADMIN_USER_ADDITION_SUCCESS_M = "User with user name {0} created successfully";
    public static final String ADMIN_USER_ADDITION_NEXT_STEP_M = "Login";
    public static final String ADMIN_USER_ADDITION_FAILURE_M = "An admin user with user name {0} already exists, please choose a different username";

    public static final String DATASOURCE_ADDITION_SUCCESS_M = "{0} datasource with label {1} created successfully";
    public static final String DATASOURCE_UPDATE_SUCCESS_M = "{0} datasource with label {1} updated successfully";

    public static final String DATASOURCE_ADDITION_FAILURE_M = "A {0} datasource with label {1} already exists, please choose a different label";
    public static final String DATASOURCE_UPDATE_FAILURE_M = "A {0} datasource with label {1} already exists, please choose a different label";

    public static final String USER_NOT_LOGGED_IN_M = "You need to login to perform this action";
    public static final String LOGIN_SUCCESS_M = "{0} logged in successfully";
    public static final String LOGIN_FAILURE_M = "Please check your username and/or password";
    public static final String LOGIN_M = "Login";
    public static final String USERNAME_VALIDATION_M = "Username should be at least of one character";
    public static final String PASSWORD_VALIDATION_M = "Password should be at least of one character";
    public static final String URL_VALIDATION_M = "url should be at least of one character";
    public static final String LABEL_VALIDATION_M = "label should be at least of one character";
    public static final String DATASOURCE_CONNECTION_SUCCESS_M = "Successfully connected to {0} datasource";
    public static final String DATASOURCE_CONNECTION_FAILURE_M = "Failed to connect to {0} datasource";
    public static final String PORT_VALIDATION_M = "Port should be greater than 0";
    public static final String PORT_M = "Port";
    public static final String QUERY_VALIDATION_M = "Query cannot be empty";
    public static final String DATASOURCE_VALIDATION_M = "Datasource cannot be empty";
    public static final String QUERY_RUN_WITH_CRON_ADDITION_SUCCESS_M = "Query has been successfully scheduled to run";
    public static final String QUERY_RUN_WITHOUT_CRON_ADDITION_SUCCESS_M = "Query has been successfully registered";
    public static final String QUERY_RUN_ADDITION_FAILURE_M = "There is already a schedule with label {0}, please choose a different label";
    public static final String KILL_QUERY_M = "Kill Query";
    public static final String KILL_M = "kill";
    public static final String KILLING_M = "killing";
    public static final String KILLED_M = "killed";
    public static final String KILL_QUERY_NOT_FOUND_M = "Query is no longer executing, hence could not kill";
    public static final String QUERY_M = "Query";
    public static final String START_M = "Start";
    public static final String DATASOURCE_M = "Datasource";
    public static final String END_M = "End";
    public static final String STATUS_M = "Status";
    public static final String RESULT_M = "Result";
    public static final String LABEL_M = "Label";
    public static final String CRON_EXPRESSION_M = "Cron Expression";
    public static final String URL_M = "URL";
    public static final String UPDATE_M = "Update";
    public static final String USER_UPDATE_SUCCESS_M = "User with user name {0} updated successfully";
    public static final String QUERY_RUN_UPDATE_SUCCESS_M = "Query has been successfully updated";
    public static final String USER_DELETE_SUCCESS_M = "User {0} deleted successfully";
    public static final String USER_DELETE_YOURSELF_M = "You cannot delete yourself";
    public static final String DELETE_M = "Delete";
    public static final String DATASOURCE_DELETE_SUCCESS_M = "Datasource {0} deleted successfully";
    public static final String DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M = "There are SQL queries which use this datasource, please delete them before deleting the datasource";
    public static final String SQL_QUERY_DELETE_SUCCESS_M = "SQL query {0} deleted successfully";
    public static final String ONBOARDING_ROOT_USER_CREATED_HTML_M = "An admin user with username <span class=\"label label-info\">{0}</span> and password <span class=\"label label-info\">{1}</span> has been created. This is the only time you will see this password, please note it down. Login with user name {0} and password {1} to complete the setup.";
    public static final String ONBOARDING_ROOT_USER_CREATED_M = "An admin user with username {0} and password {1} has been created. This is the only time you will see this password, please note it down. Login with user name {0} and password {1} to complete the setup.";
    public static final String NEXT_STEP_ADD_DATASOURCE_M = "Add Datasource";
    public static final String NEXT_STEP_ADD_JOB_M = "Add Report";
    public static final String NEXT_STEP_HEADER_M = "Next Steps";
    public static final String DATE_M = "Date";
    public static final String REPORT_M = "Report";
    public static final String REPORTS_M = "Reports";
    public static final String ONE_OFF_EXECUTION_SUCCESS_MESSAGE_M = "{0} SQL Query will be executed soon";
    public static final String EXECUTE_NOW_M = "Execute Now";
    public static final String SMTP_CONFIGURATION_ADDED_M = "SMTP configuration successfully added";
    public static final String SMTP_CONFIGURATION_UPDATED_M = "SMTP configuration successfully updated";
    public static final String SMTP_CONFIGURATION_ALREADY_PRESENT_M = "SMTP configuration is present, cannot add another one. Please edit the existing configuration";
    public static final String EMAIL_CONFIGURATION_SAVED_M = "Email configuration saved successfully";
    public static final String EMAIL_TEST_SUCCESS_M = "There were not errors while sending the mail, hop on to your inbox and confirm that you have received the mail";
    public static final String EMAIL_TEST_FAILURE_M = "Oops, got an error while trying to send mail, please check logs";
    public static final String EMAIL_TEST_SUBJECT_M = "Test mail from Kwery";
    public static final String EMAIL_TEST_BODY_M = "Congratulations, Kwery has been successfully configured to send mails.";

    public static final String REPORT_SAVE_SUCCESS_MESSAGE_M = "Report saved successfully";

    public static final String JOBAPICONTROLLER_REPORT_LABEL_EXISTS_M = "A report already exsits with label {0}, please choose a different label";
    public static final String JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M = "A SQL query already exsits with label {0}, please choose a different label";
    public static final String REPORT_SAVE_DUPLICATE_SQL_QUERY_LABEL_ERROR = "You have already used this label, please choose a different label";

    public static final String REPORT_JOB_EXECUTING_STOP_SUCCESS_M = "Report execution stopped successfully";
    public static final String REPORT_JOB_EXECUTING_STOP_FAILURE_M = "Something went wrong, report execution could not be stopped";


    public static final String DATASOURCEAPICONTROLLER_CONNECTION_ERROR_SQL_STATE_M = "SQL State";
    public static final String DATASOURCEAPICONTROLLER_CONNECTION_ERROR_ERROR_CODE_M = "Vendor error code";

    public static final String JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR_M = "Could not parse date time {0}";

    public static final String REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_START_M = "Invalid range, start is after end";
    public static final String REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_END_M = "Invalid range, end is before start";
}
