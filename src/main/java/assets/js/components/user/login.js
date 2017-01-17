define(["knockout", "jquery", "repo-dash", "router", "text!components/user/login.html"], function (ko, $, repoDash, router, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observable("");

        self.nextAction = ko.observable("");
        self.nextActionName = ko.observable("");

        self.submit = function() {
            $.ajax("/api/user/login", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    if (result.status === "success") {
                        repoDash.user.setAuthenticated(true);

                        //Intended action was not login, but the user was not authenticated, hence login page was
                        //shown. Now, post login success, refresh the page
                        if (window.location.hash !== "#user/login") {
                            window.location.reload();
                        }
                    }

                    self.status(result.status);
                    self.messages(result.messages);
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
