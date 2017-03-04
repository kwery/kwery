define(["knockout", "jquery", "repo-dash", "text!components/nav-bar.html"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;

        self.showNavBar = ko.observable(false || repoDash.user.isAuthenticated());

        self.username = ko.observable("");
        self.url = ko.observable("");

        repoDash.user.userAuthenticationBroadcaster.subscribe(function(val){
            self.showNavBar(val);
            //As soon as user logs in, we want to show email in the navbar
            self.updateEmail();
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

        self.updateEmail = function() {
            $.ajax("/api/user", {
                type: "GET",
                contentType: "application/json",
                success: function(result) {
                    self.username(result.email);
                    self.url("/#user/" + result.id);
                }
            });
        };

        //If the user refreshes the page, email is lost, hence call it outside the subscribe block
        self.updateEmail();

        return self;
    }
    return { viewModel: viewModel, template: template };
});
