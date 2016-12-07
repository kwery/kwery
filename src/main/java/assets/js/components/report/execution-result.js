define(["knockout", "jquery", "text!components/report/execution-result.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.title = ko.observable();
        self.sqlQueryExecutionResults = ko.observableArray();

        self.showReport = ko.observable(false);

        var SqlQueryExecutionResult = function(label, header, content) {
            this.label = label;
            this.header = header;
            this.content = content;
        };

        self.header = ko.observableArray([]);
        self.contents = ko.observableArray([]);

        $.ajax({
            url: "/api/job/execution/" + params.jobExecutionId,
            type: "get",
            contentType: "application/json",
            success: function (result) {
                if (result.status !== undefined) {
                    self.status(result.status);
                    self.messages(result.messages);
                } else {
                    self.showReport(true);

                    self.title(result.title);

                    ko.utils.arrayForEach(result.sqlQueryExecutionResultDtos, function(executionResult){
                        var header = executionResult.jsonResult[0];
                        var content = executionResult.jsonResult.slice(1, executionResult.jsonResult.length);
                        self.sqlQueryExecutionResults.push(new SqlQueryExecutionResult(executionResult.title, header, content));
                    });
                }
            }
        });

        return self;
    }
    return {viewModel: viewModel, template: template};
});
