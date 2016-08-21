package conf;

import controllers.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";
    public static final String ONBOARDING_WELCOME = "/module/onboarding/welcome.html";
    public static final String ONBOARDING_ADD_ADMIN_USER_HTML = "/module/onboarding/add-admin-user.html";
    public static final String ONBOARDING_ADD_ADMIN_USER_JS = "/module/onboarding/add-admin-user";
    public static final String ONBOARDING_ADD_DATASOURCE_HTML = "/module/onboarding/add-datasource.html";
    public static final String ONBOARDING_ADD_DATASOURCE_JS = "/module/onboarding/add-datasource";
    public static final String ACTION_RESULT_COMPONENT_JS = "/module/action-result";
    public static final String ACTION_RESULT_COMPONENT_HTML = "/module/action-result.html";
    public static final String ACTION_RESULT_DIALOG_COMPONENT_JS = "/module/action-result-dialog";
    public static final String ACTION_RESULT_DIALOG_COMPONENT_HTML = "/module/action-result-dialog.html";
    public static final String LOGIN_COMPONENT_HTML = "/module/user/login.html";

    public static final String ADD_ADMIN_USER_API = "/api/user/add-admin-user";
    public static final String ADD_DATASOURCE_API = "/api/datasource/add-datasource";
    public static final String LOGIN_API = "/api/user/login";

    public static final String TEMPLATE_PATH = "templatePath";
    public static final String API_PATH = "apiPath";

    @Override
    public void init(Router router) {  
        router.GET().route(INDEX).with(IndexController.class, "index");

        //Application onboarding
        router.GET().route(ONBOARDING_WELCOME).with(OnboardingController.class, "welcome");
        router.GET().route(ONBOARDING_ADD_ADMIN_USER_HTML).with(OnboardingController.class, "addAdminUserHtml");
        router.GET().route(ONBOARDING_ADD_ADMIN_USER_JS).with(OnboardingController.class, "addAdminUserJs");
        router.POST().route(ADD_ADMIN_USER_API).with(UserController.class, "addAdminUser");
        router.GET().route(ONBOARDING_ADD_DATASOURCE_HTML).with(DatasourceController.class, "addDatasourceHtml");
        router.GET().route(ONBOARDING_ADD_DATASOURCE_JS).with(DatasourceController.class, "addDatasourceJs");
        router.POST().route(ADD_DATASOURCE_API).with(DatasourceController.class, "addDatasource");

        //Custom component
        router.GET().route(ACTION_RESULT_COMPONENT_JS).with(ActionResultComponentController.class, "actionResultComponentJs");
        router.GET().route(ACTION_RESULT_COMPONENT_HTML).with(ActionResultComponentController.class, "actionResultComponentHtml");
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_JS).with(ActionResultComponentController.class, "actionResultDialogComponentJs");
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_HTML).with(ActionResultComponentController.class, "actionResultDialogComponentHtml");

        router.POST().route(LOGIN_API).with(UserController.class, "login");
        router.GET().route(LOGIN_COMPONENT_HTML).with(UserController.class, "loginHtml");

        //Static asset
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    }
}
