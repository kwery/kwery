define(["knockout", "jquery", "text!components/sql-query/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.executions = ko.observableArray([]);
        self.sqlQuery = ko.observable("");

        $.ajax({
            url: "/api/sql-query/1/execution/0/100",
            type: "get",
            contentType: "application/json",
            success: function (executionListDto) {
                ko.utils.arrayPushAll(self.executions, executionListDto.sqlQueryExecutionDtos);
                self.sqlQuery(executionListDto.sqlQuery);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
