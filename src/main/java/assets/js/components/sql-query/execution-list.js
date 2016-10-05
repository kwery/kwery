define(["knockout", "jquery", "text!components/sql-query/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.executions = ko.observableArray([]);
        self.sqlQuery = ko.observable("");

        //Filter form parameters
        self.executionStartStart = ko.observable("");
        self.executionStartEnd = ko.observable("");
        self.executionEndStart = ko.observable("");
        self.executionEndEnd = ko.observable("");
        self.statuses = ko.observableArray([]);
        self.pageNumber = ko.observable(0);
        self.resultCount = ko.observable(10);

        self.getExecutionList = function() {
            $.ajax("/api/sql-query/" + params.sqlQueryId + "/execution", {
                data: ko.toJSON({
                    executionStartStart: self.executionStartStart(),
                    executionStartEnd: self.executionStartEnd(),
                    executionEndStart: self.executionEndStart(),
                    executionEndEnd: self.executionEndEnd(),
                    statuses: self.statuses(),
                    pageNumber: self.pageNumber(),
                    resultCount: self.resultCount()
                }),
                type: "post", contentType: "application/json",
                success: function(result) {
                    self.executions(result.sqlQueryExecutionDtos);
                    self.sqlQuery(result.sqlQuery);
                }
            });
        };

        self.getExecutionList();

        self.submit = function(formElem) {
            self.getExecutionList();
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
