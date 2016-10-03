define(["knockout", "jquery", "text!components/sql-query/executing.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.executions = ko.observableArray([]);

        $.ajax({
            url: "/api/sql-query/executing",
            type: "get",
            contentType: "application/json",
            success: function (executions) {
                ko.utils.arrayPushAll(self.executions, executions);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
