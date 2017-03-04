define(["knockout", "jquery", "text!components/onboarding/onboarding.html", "ajaxutil"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.showNextSteps = ko.observable(false);
        self.showAddDatasource = ko.observable(false);
        self.showAddJob = ko.observable(false);

        self.message = ko.observable(ko.i18n("onboarding.default.message"));

        ajaxUtil.waitingAjax({
            url: "/api/onboarding/next-action",
            type: "GET",
            contentType: "application/json",
            success: function(response){
                switch (response.action) {
                    case "LOGIN":
                        self.showNextSteps(false);
                        window.location.href = "/#user/login";
                        break;
                    case "SIGN_UP":
                        self.showNextSteps(false);
                        window.location.href = "/#user/sign-up?onboarding=true";
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

        return self;
    }
    return { viewModel: viewModel, template: template };
});
