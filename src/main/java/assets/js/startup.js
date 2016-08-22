define(["jquery-migrate", "knockout", "./router", "knockout-projections"], function ($, ko, router) {
    ko.components.register("onboarding-welcome", {
        template: {
            require: "text!/module/onboarding/welcome.html"
        }
    });
    ko.components.register("action-result", {
        require: "/module/action-result"
    });
    ko.components.register("action-result-dialog", {
        require: "/module/action-result-dialog"
    });
    ko.components.register("onboarding-add-admin-user", {
        require: "/module/add-admin-user"
    });
    ko.components.register("onboarding-add-datasource", {
        require: "/module/add-datasource"
    });
    ko.components.register("user-login", {
        require: "/module/user-login"
    });
    ko.applyBindings({ route: router.currentRoute });
});
