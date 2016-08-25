define(["knockout", "jquery", "repo-dash", "text!${templatePath}"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");

        self.repoDash = repoDash;

        self.submit = function() {
            $.ajax("${apiPath}", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.message(result.message);
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
