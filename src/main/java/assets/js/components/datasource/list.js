define(["knockout", "jquery", "text!components/datasource/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.datasources = ko.observableArray([]);

        $.ajax({
            url: "/api/datasource/all",
            type: "get",
            contentType: "application/json",
            success: function (result) {
                self.datasources(result);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
