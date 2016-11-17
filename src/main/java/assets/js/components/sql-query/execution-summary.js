define(["knockout", "jquery", "text!components/sql-query/execution-summary.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.executions = ko.observableArray([]);

        $.ajax("/api/sql-query/latest-execution", {
            type: "GET",
            contentType: "application/json",
            success: function(result) {
                result.map(function(obj){
                    obj.resultLink = "/#sql-query/" + obj.sqlQueryId + "/execution/" + obj.sqlQueryExecutionId;
                    return obj;
                });
                self.executions(result);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
