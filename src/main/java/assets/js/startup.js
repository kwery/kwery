define(["jquery-migrate", "knockout", "router", "polyglot", "jquery-validate", "knockout-projections"], function ($, ko, router, polyglot) {
    ko.components.register("onboarding-welcome", {
        template: {
            require: "text!components/onboarding/welcome.html"
        }
    });
    ko.components.register("action-result", {
        require: "components/action-result"
    });
    ko.components.register("kill-sql-query", {
        require: "components/kill-sql-query"
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
    ko.components.register("sql-query-add", {
        require: "components/sql-query/add"
    });
    ko.components.register("sql-query-executing", {
        require: "components/sql-query/executing"
    });
    ko.components.register("sql-query-execution-list", {
        require: "components/sql-query/execution-list"
    });
    ko.components.register("sql-query-execution-result", {
        require: "components/sql-query/execution-result"
    });
    ko.components.register("sql-query-list", {
        require: "components/sql-query/list"
    });
    ko.components.register("datasource-list", {
        require: "components/datasource/list"
    });
    ko.components.register("user-list", {
        require: "components/user/list"
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
