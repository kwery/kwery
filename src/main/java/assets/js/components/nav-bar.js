define(["knockout", "jquery", "repo-dash", "text!components/nav-bar.html"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;

        self.showNavBar = ko.observable(false || repoDash.user.isAuthenticated());

        self.username = ko.observable("");
        self.url = ko.observable("");

        repoDash.user.userAuthenticationBroadcaster.subscribe(function(val){
            self.showNavBar(val);
            $.ajax("/api/user", {
                type: "GET",
                contentType: "application/json",
                success: function(result) {
                    self.username(result.email);
                    self.url("/#user/" + result.id);
                }
            });
        }, this, "userLogin");

        self.logout = function() {
            $.ajax("/api/user/logout", {
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    repoDash.user.setAuthenticated(false);
                    window.location = "/";
                }
            });
            return false;
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
