define(["jquery-migrate", "knockout", "router", "polyglot", "knockout-projections"], function ($, ko, router, polyglot) {
    ko.components.register("onboarding-welcome", {
        template: {
            require: "text!/module/onboarding/welcome.html"
        }
    });
    ko.components.register("action-result", {
        require: "/module/action-result"
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
