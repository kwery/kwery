define(["knockout", "jquery", "text!components/onboarding/onboarding.html", "knockout-jqueryui/dialog"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.message = ko.observable(ko.i18n("onboarding.welcome"));

        $.ajax({
            url: "/api/onboarding/next-action",
            type: "GET",
            contentType: "application/json",
            success: function(response){
                switch (response.action) {
                    case "ADD_ADMIN_USER":
                        self.addAdminUser();
                        break;
                    case "ADD_DATASOURCE":
                        window.location.href = "/#onboarding/add-datasource";
                        break;
                    case "ADD_SQL_QUERY":
                        window.location.href = "/#sql-query/add";
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

        return self;
    }
    return { viewModel: viewModel, template: template };
});
