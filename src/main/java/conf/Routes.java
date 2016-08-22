package conf;

import controllers.IndexController;
import controllers.apis.DatasourceApiController;
import controllers.apis.UserApiController;
import controllers.modules.actionresult.ActionResultModuleController;
import controllers.modules.actionresultdialog.ActionResultDialogModuleController;
import controllers.modules.datasource.DatasourceAddModuleController;
import controllers.modules.onboarding.OnboardingModuleController;
import controllers.modules.user.addadmin.UserAddAdminModuleController;
import controllers.modules.user.login.UserLoginModuleController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";

    public static final String ONBOARDING_WELCOME = "/module/onboarding/welcome.html";

    public static final String ADD_ADMIN_USER_HTML = "/module/add-admin-user.html";
    public static final String ADD_ADMIN_USER_JS = "/module/add-admin-user";

    public static final String ADD_DATASOURCE_HTML = "/module/add-datasource.html";
    public static final String ADD_DATASOURCE_JS = "/module/add-datasource";

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

    public static final String MODULE_REQUEST_PREFIX = "/module";
    public static final String API_REQUEST_PREFIX = "/api";

    @Override
    public void init(Router router) {  
        router.GET().route(INDEX).with(IndexController.class, "index");

        //Module - Start
        //Onboarding
        router.GET().route(ONBOARDING_WELCOME).with(OnboardingModuleController.class, "html");

        //Admin user module
        router.GET().route(ADD_ADMIN_USER_HTML).with(UserAddAdminModuleController.class, "html");
        router.GET().route(ADD_ADMIN_USER_JS).with(UserAddAdminModuleController.class, "js");

        //Datasource module
        router.GET().route(ADD_DATASOURCE_HTML).with(DatasourceAddModuleController.class, "html");
        router.GET().route(ADD_DATASOURCE_JS).with(DatasourceAddModuleController.class, "js");

        //Action result module
        router.GET().route(ACTION_RESULT_COMPONENT_JS).with(ActionResultModuleController.class, "js");
        router.GET().route(ACTION_RESULT_COMPONENT_HTML).with(ActionResultModuleController.class, "html");

        //Action result dialog module
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_JS).with(ActionResultDialogModuleController.class, "js");
        router.GET().route(ACTION_RESULT_DIALOG_COMPONENT_HTML).with(ActionResultDialogModuleController.class, "html");

        //Login module
        router.GET().route(LOGIN_COMPONENT_HTML).with(UserLoginModuleController.class, "html");
        //Module - End

        //Api - Start
        router.POST().route(ADD_ADMIN_USER_API).with(UserApiController.class, "addAdminUser");
        router.POST().route(LOGIN_API).with(UserApiController.class, "login");
        router.POST().route(ADD_DATASOURCE_API).with(DatasourceApiController.class, "addDatasource");
        //Api - End

        //Static asset
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
    }
}
