define(["knockout", "jquery", "text!components/datasource/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.datasources = ko.observableArray([]);
        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        $.ajax({
            url: "/api/datasource/all",
            type: "get",
            contentType: "application/json",
            success: function (result) {
                self.datasources(result);
            }
        });

        self.delete = function(datasource) {
            $.ajax({
                url: "/api/datasource/delete/" + datasource.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages || []);

                    if (result.status === "success") {
                        self.datasources.remove(datasource);
                    }
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
