define(["knockout", "jquery", "text!components/onboarding/onboarding.html", "knockout-jqueryui/dialog"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.showLogin = ko.observable(false);
        self.showNextSteps = ko.observable(false);
        self.showAddDatasource = ko.observable(false);
        self.showAddJob = ko.observable(false);

        self.message = ko.observable(ko.i18n("onboarding.default.message"));

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
                        self.showAddJob(true);
                        self.showAddDatasource(true);
                        break;
                    case "ADD_JOB":
                        self.showNextSteps(true);
                        self.showAddJob(true);
                        break;
                    case "SHOW_HOME_SCREEN":
                        window.location.href = "/#report/list";
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
            window.location.href = "/#onboarding";
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
