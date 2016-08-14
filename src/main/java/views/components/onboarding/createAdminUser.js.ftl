define(["knockout", "jquery", "text!/onboarding/create-admin-user.html"], function (ko, $, onboardingTemplate) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.successMessage = ko.observable("");
        self.failureMessage = ko.observable("");
        self.save = function() {
            //This is done if someone tries to create the user again
            self.successMessage("");
            self.failureMessage("");
            $.ajax("/onboarding/create-admin-user", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    if (result.status) {
                        self.successMessage(result.message);
                    } else {
                        self.failureMessage(result.message);
                    }
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: onboardingTemplate };
});
