package com.kwery.tests.util;

/**
 * Holds messages present in messages.properties file, this is done to ease testing. All messages present here
 * should have an equivalent entry in messages.properties file.
 *
 * Convention for key name is to convert the corresponding key in messages.properties to upper case, replace dots
 * with hyphens and append _M. Append _HTML_M if the message is HTML.
 */
public class Messages {
    public static final String DATASOURCE_ADDITION_SUCCESS_M = "{0} datasource with name {1} created successfully";
    public static final String DATASOURCE_UPDATE_SUCCESS_M = "{0} datasource with name {1} updated successfully";
    public static final String DATASOURCE_ADDITION_FAILURE_M = "A {0} datasource with name {1} already exists, please choose a different name";

    public static final String USER_NOT_LOGGED_IN_M = "You need to login to perform this action";
    public static final String LOGIN_SUCCESS_M = "Login successful, redirecting...";
    public static final String LOGIN_FAILURE_M = "Please check your email and/or password";
    public static final String USERNAME_VALIDATION_M = "Username should be at least of one character";
    public static final String URL_VALIDATION_M = "url should be at least of one character";
    public static final String LABEL_VALIDATION_M = "label should be at least of one character";
    public static final String PORT_VALIDATION_M = "Port should be greater than 0";
    public static final String QUERY_M = "Query";
    public static final String START_M = "Start";
    public static final String DATASOURCE_M = "Datasource";
    public static final String END_M = "End";
    public static final String STATUS_M = "Status";
    public static final String LABEL_M = "Label";
    public static final String USER_DELETE_SUCCESS_M = "User {0} deleted successfully";
    public static final String USER_DELETE_YOURSELF_M = "You cannot delete yourself";
    public static final String DATASOURCE_DELETE_SUCCESS_M = "Datasource {0} deleted successfully";
    public static final String DATASOURCE_DELETE_SQL_QUERIES_PRESENT_M = "There are SQL queries which use this datasource, please delete them before deleting the datasource";
    public static final String SQL_QUERY_DELETE_SUCCESS_M = "SQL query {0} deleted successfully";
    public static final String NEXT_STEP_ADD_DATASOURCE_M = "Add Datasource";
    public static final String NEXT_STEP_ADD_JOB_M = "Add Report";
    public static final String NEXT_STEP_HEADER_M = "Next Steps";
    public static final String DATE_M = "Date";
    public static final String REPORT_M = "Report";
    public static final String ONE_OFF_EXECUTION_SUCCESS_MESSAGE_M = "{0} SQL Query will be executed soon";
    public static final String SMTP_CONFIGURATION_ADDED_M = "SMTP configuration successfully added";
    public static final String SMTP_CONFIGURATION_UPDATED_M = "SMTP configuration successfully updated";
    public static final String SMTP_CONFIGURATION_ALREADY_PRESENT_M = "SMTP configuration is present, cannot add another one. Please edit the existing configuration";
    public static final String EMAIL_CONFIGURATION_SAVED_M = "Sender details configuration saved successfully";
    public static final String EMAIL_TEST_SUCCESS_M = "Email sent successfully, hop on to your inbox and confirm that you have received the mail";
    public static final String EMAIL_TEST_SUBJECT_M = "Test mail from Kwery";
    public static final String EMAIL_TEST_BODY_M = "Congratulations, Kwery has been successfully configured to send mails.";

    public static final String REPORT_SAVE_SUCCESS_MESSAGE_M = "Report template save successfully";

    public static final String JOBAPICONTROLLER_REPORT_NAME_EXISTS_M = "A report template already exists with name {0}, please choose a different name";
    public static final String JOBAPICONTROLLER_SQL_QUERY_LABEL_EXISTS_M = "A SQL query already exists with name {0}, please choose a different name";
    public static final String REPORT_SAVE_DUPLICATE_SQL_QUERY_LABEL_ERROR = "You have already used this name, please choose a different name";

    public static final String REPORT_JOB_EXECUTING_STOP_SUCCESS_M = "Report generation stopped successfully";
    public static final String REPORT_JOB_EXECUTING_STOP_FAILURE_M = "Something went wrong, report generation could not be stopped";

    public static final String JOBAPICONTROLLER_FILTER_DATE_PARSE_ERROR_M = "Could not parse date time {0}";

    public static final String REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_START_M = "Invalid range, start is after end";
    public static final String REPORT_JOB_EXECUTION_FILTER_INVALID_RANGE_END_M = "Invalid range, end is before start";

    public static final String JOBAPICONTROLLER_DELETE_JOB_HAS_CHILDREN_M = "There are other report templates which depend on this job, please remove the dependencies before deleting this job";
    public static final String REPORT_LIST_DELETE_SUCCESS_M = "Report template deleted successfully";

    public static final String REPORT_LABEL_SAVE_SUCCESS_M = "Label {0} saved successfully";

    public static final String JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M = "There are report templates which have label {0}";
    public static final String JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M = "There are labels which are nested under {0}";
    public static final String JOBLABELAPICONTROLLER_DELETE_SUCCESS_M = "Label {0} deleted successfully";

    public static final String JOBLABELAPICONTROLLER_INVALID_CRON_EXPRESSION_M = "{0} is not a valid cron expression";

    public static final String JOBAPICONTROLLER_REPORT_NOT_FOUND_M = "Report template not found";

    public static final String URL_CONFIGURATION_SAVE_SUCCESS_M = "Kwery URL saved successfully";

    public static final String REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M = "Report Generation Failed";
    public static final String REPORT_GENERATION_FAILURE_ALERT_EMAIL_BODY_M = "Failed Report";

    public static final String REPORT_JOB_EXECUTION_DELETE_M = "Report execution deleted successfully";

    public static final String JOBAPICONTROLLER_REPORT_CONTENT_LARGE_WARNING_M = "Report too large to display, please download and view";

    public static final String REPORTEMAILSENDER_ATTACHMENT_SKIPPED_M = "P.S. Some attachments were not sent as the files were too large";

    public static final String REPORT_LIST_SEARCH_INVALID_M = "Search string contains invalid characters";

    public static final String SIGN_UP_SUCCESS_MESSAGE_M = "Signup successful, please login with the email and password";

    public static final String SIGN_UP_FAILURE_MESSAGE_M = "A user already exists with the email {0}";

    public static final String SIGN_UP_UPDATE_SUCCESS_MESSAGE_M = "User updated successfully";

    public static final String ONBOARDING_USER_ADD_M = "You made a great choice in using Kwery, let us create a user as the first step";
    public static final String ONBOARDING_DATASOURCE_ADD_M = "You are almost there, let us add a datasource";
    public static final String ONBOARDING_REPORT_ADD_M = "This is the last step, let us add a report";
    public static final String ONBOARDING_REPORT_ADD_POST_DATASOURCE_M = "Datasource added successfully, this is the last step, let us add a report";

    public static final String USER_EDIT_PASSWORD_SUCCESS_MESSAGE_M = "Password reset successfully";
    public static final String USER_EDIT_SUPERUSER_SUCCESS_MESSAGE_M = "User saved successfully";

    public static final String  USER_EDIT_SUPERUSER_FAILURE_MESSAGE_M = "Cannot demote from admin, at least one user has to be an admin";

    public static final String EMAIL_CONFIGURATION_SMTP_MISSING_M = "Please configure SMTP";
    public static final String EMAIL_CONFIGURATION_SENDER_DETAILS_MISSING_M = "Please configure sender details";
}
