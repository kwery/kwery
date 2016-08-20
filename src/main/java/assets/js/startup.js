define(["jquery-migrate", "knockout", "./router", "knockout-projections"], function ($, ko, router) {
    ko.components.register("greeter", {
        require: "components/greeter/greeting"
    });
    ko.components.register("onboardingWelcome", {
        template: { require: "text!/onboarding/welcome" }
    });
    ko.components.register("settings", {
        template: { require: "text!components/settings/settings.html" }
    });
    ko.components.register("actionresult", {
            require: "/component/actionresultcomponent"
        }
    );
    ko.components.register("actionresultdialog", {
            require: "/component/actionresultdialogcomponent"
        }
    );
    ko.components.register("onboardingAddAdminUser", {
        require: "/onboarding/add-admin-user"
    });
    ko.components.register("onboardingAddDatasource", {
        require: "/onboarding/add-datasource"
    });
    ko.applyBindings({ route: router.currentRoute });
});
