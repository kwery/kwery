define(["knockout", "jquery", "text!${templatePath}"], function (ko, $, onboardingTemplate) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");
        self.nextAction = ko.observable("");
        self.nextActionName = ko.observable("");

        self.save = function() {
            //This is done if someone tries to create the user again
            $.ajax("${apiPath}", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    //Clear previous set value
                    self.status("");
                    self.message("");
                    self.nextAction("");
                    self.nextActionName("");

                    self.status(result.status);
                    self.message(result.message);
                    self.nextAction(result.nextAction);
                    self.nextActionName(result.nextActionName);
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: onboardingTemplate };
});
