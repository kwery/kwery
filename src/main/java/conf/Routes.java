package conf;

import controllers.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";
    public static final String ONBOARDING_WELCOME = "/onboarding/welcome";
    public static final String ONBOARDING_ADD_ADMIN_USER_HTML = "/onboarding/add-admin-user.html";
    public static final String ONBOARDING_ADD_ADMIN_USER_JS = "/onboarding/add-admin-user";
    public static final String ONBOARDING_ADD_ADMIN_USER = "/onboarding/add-admin-user";
    public static final String ONBOARDING_ADD_DATASOURCE_HTML = "/onboarding/add-datasource.html";
    public static final String ONBOARDING_ADD_DATASOURCE_JS = "/onboarding/add-datasource";
    public static final String ACTION_RESULT_COMPONENT_JS = "/component/actionresultcomponent";
    public static final String ACTION_RESULT_COMPONENT_HTML = "/component/actionresultcomponent.html";
    public static final String ACTION_RESULT_DIALOG_COMPONENT_JS = "/component/actionresultdialogcomponent";
    public static final String ACTION_RESULT_DIALOG_COMPONENT_HTML = "/component/actionresultdialogcomponent.html";
    public static final String ONBOARDING_ADD_DATASOURCE = "/onboarding/add-datasource";

    @Override
    public void init(Router router) {  
        router.GET().route(INDEX).with(IndexController.class, "index");

        //Application onboarding
        router.GET().route(ONBOARDING_WELCOME).with(OnboardingController.class, "welcome");
        router.GET().route(ONBOARDING_ADD_ADMIN_USER_HTML).with(OnboardingController.class, "addAdminUserHtml");
        router.GET().route(ONBOARDING_ADD_ADMIN_USER_JS).with(OnboardingController.class, "addAdminUserJs");
        router.POST().route(ONBOARDING_ADD_ADMIN_USER).with(UserController.class, "addAdminUser");
        router.GET().route(ONBOARDING_ADD_DATASOURCE_HTML).with(DatasourceController.class, "addDatasourceHtml");
        router.GET().route(ONBOARDING_ADD_DATASOURCE_JS).with(DatasourceController.class, "addDatasourceJs");
        router.POST().route(ONBOARDING_ADD_DATASOURCE).with(DatasourceController.class, "addDatasource");

        //Custom component
        router.GET().route(ACTION_RESULT_COMPONENT_JS).with(ActionResultComponentController.class, "actionResultComponentJs");
        router.GET().route(ACTION_RESULT_COMPONENT_HTML).with(ActionResultComponentController.class, "actionResultComponentHtml");
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_JS).with(ActionResultComponentController.class, "actionResultDialogComponentJs");
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_HTML).with(ActionResultComponentController.class, "actionResultDialogComponentHtml");

        //Static asset
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    }
}
