define(["jquery-migrate", "knockout", "router", "polyglot", "jquery-validate", "knockout-projections", "bootstrap"], function ($, ko, router, polyglot) {
    ko.components.register("nav-bar", {
        require: "components/nav-bar"
    });
    ko.components.register("onboarding", {
        require: "components/onboarding/onboarding"
    });
    ko.components.register("action-result", {
        require: "components/action-result"
    });
    ko.components.register("panel-add-link", {
        require: "components/panel-add-link"
    });
    ko.components.register("user-add", {
        require: "components/user/admin/add"
    });
    ko.components.register("datasource-add", {
        require: "components/datasource/add"
    });
    ko.components.register("user-login", {
        require: "components/user/login"
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
    ko.components.register("sql-query-execution-summary", {
        require: "components/sql-query/execution-summary"
    });
    ko.components.register("datasource-list", {
        require: "components/datasource/list"
    });
    ko.components.register("user-list", {
        require: "components/user/list"
    });
    ko.components.register("user-update", {
        require: "components/user/update"
    });
    ko.components.register("email-configuration", {
        require: "components/email/configuration"
    });
    ko.components.register("report-add", {
        require: "components/report/add"
    });
    ko.components.register("report-list", {
        require: "components/report/list"
    });
    ko.components.register("report-executing", {
        require: "components/report/executing"
    });
    ko.components.register("report-execution-list", {
        require: "components/report/execution-list"
    });
    ko.components.register("report-execution-result", {
        require: "components/report/execution-result"
    });
    ko.components.register("job-label-add", {
        require: "components/job-label/add"
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
