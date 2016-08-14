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


import controllers.OnboardingController;
import controllers.UserController;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;

public class Routes implements ApplicationRoutes {
    public static final String INDEX = "/";
    public static final String WELCOME = "/welcome";
    public static final String CREATE_ADMIN_USER = "/create-admin-user";

    @Override
    public void init(Router router) {  
        router.GET().route(INDEX).with(ApplicationController.class, "index");

        router.GET().route("/onboarding/welcome").with(OnboardingController.class, "welcome");
        router.GET().route("/onboarding/create-admin-user.html").with(OnboardingController.class, "createAdminUserHtml");
        router.GET().route("/onboarding/create-admin-user").with(OnboardingController.class, "createAdminUserJs");
        router.POST().route("/onboarding/create-admin-user").with(UserController.class, "createAdminUser");

        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

        // Index / Catchall shows index page
        router.GET().route("/.*").with(ApplicationController.class, "index");
    }

}
