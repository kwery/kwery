define(["knockout", "jquery", "text!/onboarding/create-admin-user.html"], function (ko, $, onboardingTemplate) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.sM = ko.observable("");
        self.fM = ko.observable("");

        self.save = function() {
            //This is done if someone tries to create the user again
            $.ajax("/onboarding/create-admin-user", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    if (result.status) {
                        self.sM(result.message);
                    } else {
                        self.fM(result.message);
                    }
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: onboardingTemplate };
});
