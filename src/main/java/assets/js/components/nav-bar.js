define(["knockout", "jquery", "repo-dash", "text!components/nav-bar.html"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;

        self.showNavBar = ko.observable(false || repoDash.user.isAuthenticated());

        repoDash.user.userAuthenticationBroadcaster.subscribe(function(val){
            self.showNavBar(val);
        }, this, "userLogin");

        self.username = ko.observable("");
        self.url = ko.observable("");

        $.ajax("/api/user", {
            type: "GET",
            contentType: "application/json",
            success: function(result) {
                self.username(result.username);
                self.url("/#user/update/" + result.id);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
