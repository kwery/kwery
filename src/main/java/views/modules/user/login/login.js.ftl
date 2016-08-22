define(["knockout", "jquery", "text!${templatePath}"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");

        self.login = function() {
            $.ajax("${apiPath}", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    //Clear previous set value
                    self.status("");
                    self.message("");

                    self.status(result.status);
                    self.message(result.message);
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
