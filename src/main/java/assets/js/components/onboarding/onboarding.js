define(["knockout", "jquery", "text!components/onboarding/onboarding.html", "ajaxutil"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        ajaxUtil.waitingAjax({
            url: "/api/onboarding/next-action",
            type: "GET",
            contentType: "application/json",
            success: function(response){
                switch (response.action) {
                    case "LOGIN":
                        window.location.href = "/#user/login";
                        break;
                    case "SIGN_UP":
                        window.location.href = "/#user/sign-up?onboarding=true";
                        break;
                    case "ADD_DATASOURCE":
                        window.location.href = "/#datasource/add?onboarding=true";
                        break;
                    case "ADD_JOB":
                        window.location.href = "/#report/add?onboarding=true";
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
