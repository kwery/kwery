package controllers.util;

/**
 * Holds messages present in messages.properties file, this is done to ease testing. All messages present here
 * should have an equivalent entry in messages.properties file.
 *
 * Convention for key name is to convert the corresponding key in messages.properties to upper case, replace dots
 * with hyphens and append _M.
 */
public class Messages {
    public static final String HEADER_TITLE_M = "Hello World!";
    public static final String HEADER_HOME_M = "BAM!";
    public static final String HELLO_WORLD_M = "Hello World!";
    public static final String HELLO_WORLD_JSON_M = "There is also Json rendering you can check out!";
    public static final String INSTALLATION_WELCOME_M = "Welcome to RepoDash, reporting, dashboarding and visualistion made easy. As the first step, let us create an administrative user to manage the application.";
    public static final String CREATE_ADMIN_USER_M = "Create Admin User";
    public static final String USER_NAME_M = "User Name";
    public static final String PASSWORD_M = "Password";
    public static final String CREATE_M = "create";
    public static final String ADMIN_USER_CREATION_SUCCESS_M = "Admin user with user name {0} created successfully";
    public static final String ADMIN_USER_CREATION_FAILURE_M = "An admin user with user name {0} already exists, please choose a different username";
}
