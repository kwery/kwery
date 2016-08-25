define(["jquery-migrate", "knockout", "router", "polyglot", "knockout-projections"], function ($, ko, router, polyglot) {
    ko.components.register("onboarding-welcome", {
        template: {
            require: "text!components/onboarding/welcome.html"
        }
    });
    ko.components.register("action-result", {
        require: "components/action-result"
    });
    ko.components.register("onboarding-add-admin-user", {
        require: "components/user/admin/add"
    });
    ko.components.register("onboarding-add-datasource", {
        require: "components/datasource/add"
    });
    ko.components.register("user-login", {
        require: "components/user/login"
    });

    ko.applyBindings({ route: router.currentRoute });

    //Attach polyglot for i18n to ko
    (function i18n(Polyglot) {
        var polyglot = new Polyglot({phrases: dashRepoMessages});
        ko.i18n = function(key, options) {
            if (options === undefined) {
                options = {};
            }

            return polyglot.t(key, options);
        };
    })(polyglot);
});
