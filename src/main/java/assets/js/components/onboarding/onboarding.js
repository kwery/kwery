define(["knockout", "jquery", "text!components/onboarding/onboarding.html", "knockout-jqueryui/dialog"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.showLogin = ko.observable(false);
        self.showNextSteps = ko.observable(false);
        self.showAddDatasource = ko.observable(false);
        self.showAddSqlQuery = ko.observable(false);

        self.message = ko.observable(ko.i18n("onboarding.welcome"));
        self.heading = ko.observable(ko.i18n("welcome"));

        $.ajax({
            url: "/api/onboarding/next-action",
            type: "GET",
            contentType: "application/json",
            success: function(response){
                switch (response.action) {
                    case "ADD_ADMIN_USER":
                        self.addAdminUser();
                        self.showLogin(true);
                        break;
                    case "ADD_DATASOURCE":
                        self.showNextSteps(true);
                        self.showAddSqlQuery(true);
                        self.showAddDatasource(true);
                        break;
                    case "ADD_SQL_QUERY":
                        self.showNextSteps(true);
                        self.showAddSqlQuery(true);
                        break;
                    case "SHOW_EXECUTING_QUERIES":
                        window.location.href = "/#sql-query/executing";
                        break;
                }
            }
        });

        self.addAdminUser = function() {
            $.ajax({
                url: "/api/onboarding/user/add",
                type: "POST",
                contentType: "application/json",
                success: function(actionResult){
                    //TODO - else case
                    if (actionResult.status === 'success') {
                        self.message(actionResult.messages[0]);
                    }
                }
            });
        };

        self.login = function() {
            window.location.href = "/#user/login";
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
