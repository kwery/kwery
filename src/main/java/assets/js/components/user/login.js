define(["knockout", "jquery", "repo-dash", "router", "text!components/user/login.html"], function (ko, $, repoDash, router, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");

        self.onSuccessShowDialog = ko.observable(true);
        self.nextAction = ko.observable("");
        self.nextActionName = ko.observable("");

        self.submit = function() {
            $.ajax("/api/user/login", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    //Clear previous set value
                    self.status("");
                    self.message("");

                    if (result.status === "success") {
                        repoDash.user.setAuthenticated(true);

                        //Intended action was not login, but the user was not authenticated, hence login page was
                        //shown. Now, post login success, route to the intended page.
                        if (window.location.hash !== "#user/login") {
                            var comp = repoDash.componentMapping.component(params[0].previous);
                            router.currentRoute({page: comp});
                            //router.currentRoute({page: 'onboarding-add-datasource'});
                        }
                    }

                    self.status(result.status);
                    self.message(result.message);
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
