define(["knockout", "jquery", "text!components/datasource/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.url = ko.observable();
        self.label = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

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
                    self.status(result.status);
                    self.messages(result.messages);
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});

