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
                self.url("/#user/" + result.id);
            }
        });

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
