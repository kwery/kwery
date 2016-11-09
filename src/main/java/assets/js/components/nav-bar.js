define(["knockout", "jquery", "repo-dash", "text!components/nav-bar.html"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;

        self.showNavBar = ko.observable(false || repoDash.user.isAuthenticated());

        repoDash.user.userAuthenticationBroadcaster.subscribe(function(val){
            self.showNavBar(val);
        }, this, "userLogin");

        return self;
    }
    return { viewModel: viewModel, template: template };
});
