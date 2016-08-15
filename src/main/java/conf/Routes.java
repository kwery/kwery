/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package conf;


import controllers.ActionResultComponentController;
import controllers.OnboardingController;
import controllers.UserController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.IndexController;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";
    public static final String WELCOME = "/welcome";
    public static final String CREATE_ADMIN_USER = "/create-admin-user";
    public static final String ONBOARDING_WELCOME = "/onboarding/welcome";
    public static final String ONBOARDING_CREATE_ADMIN_USER_HTML = "/onboarding/create-admin-user.html";
    public static final String ONBOARDING_CREATE_ADMIN_USER_JS = "/onboarding/create-admin-user";
    public static final String ONBOARDING_CREATE_ADMIN_USER = "/onboarding/create-admin-user";
    public static final String ACTION_RESULT_COMPONENT_JS = "/component/actionresultcomponent";
    public static final String ACTION_RESULT_COMPONENT_HTML = "/component/actionresultcomponent.html";

    @Override
    public void init(Router router) {  
        router.GET().route(INDEX).with(IndexController.class, "index");

        //Application onboarding
        router.GET().route(ONBOARDING_WELCOME).with(OnboardingController.class, "welcome");
        router.GET().route(ONBOARDING_CREATE_ADMIN_USER_HTML).with(OnboardingController.class, "createAdminUserHtml");
        router.GET().route(ONBOARDING_CREATE_ADMIN_USER_JS).with(OnboardingController.class, "createAdminUserJs");
        router.POST().route(ONBOARDING_CREATE_ADMIN_USER).with(UserController.class, "createAdminUser");

        //Custom component
        router.GET().route(ACTION_RESULT_COMPONENT_JS).with(ActionResultComponentController.class, "actionResultComponentJs");
        router.GET().route(ACTION_RESULT_COMPONENT_HTML).with(ActionResultComponentController.class, "actionResultComponentHtml");

        //Static asset
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

        // Index / Catchall
        router.GET().route("/.*").with(IndexController.class, "index");
    }
}
