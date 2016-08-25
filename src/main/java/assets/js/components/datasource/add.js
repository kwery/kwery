define(["knockout", "jquery", "text!js/components/datasource/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.url = ko.observable();
        self.label = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");

        self.submit = function() {
            $.ajax("/api/datasource/add-datasource", {
                data: ko.toJSON({
                    url: self.url,
                    username: self.username,
                    password: self.password,
                    label: self.label,
                    type: "MYSQL"
                }),
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

