define(["knockout", "jquery", "text!components/sql-query/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        //Pagination
        //No of results to show in the page
        var RESULT_COUNT = 4;

        self.totalCount = ko.observable(0);
        self.pageNumber = ko.observable(0);

        //Even if the value does not change, call the page number subscriber function.
        //This is needed because we get the result list on page number update
        //If we are on the default page, apply filter and submit we update pageNumber from 0 to 0
        //but we still want the execution list to be fetched which is hooked to pageNumber change
        //subscription.
        self.pageNumber.extend({notify: "always"});

        //Get result list on every pagination click
        self.pageNumber.subscribe(function(){
            self.getExecutionList();
        });

        self.resultCount = ko.observable(RESULT_COUNT);

        self.hasNext = ko.computed(function(){
            return self.totalCount() > ((self.pageNumber() + 1) * self.resultCount());
        }, self);

        self.hasPrevious = ko.computed(function(){
            return self.pageNumber() > 0;
        }, self);

        //Can be changed only on the first page, cannot be changed in between pagination
        self.canChangeResultCount = ko.computed(function(){
            return self.pageNumber() == 0;
        }, self);

        self.executions = ko.observableArray([]);
        self.sqlQuery = ko.observable("");

        //Filter form parameters
        self.executionStartStart = ko.observable("");
        self.executionStartEnd = ko.observable("");
        self.executionEndStart = ko.observable("");
        self.executionEndEnd = ko.observable("");
        self.statuses = ko.observableArray([]);

        //Reset the page on filter request
        self.submit = function(formElem) {
            self.pageNumber(0);
        };

        self.previous = function() {
            self.pageNumber(self.pageNumber() - 1);
        };

        self.next = function() {
            self.pageNumber(self.pageNumber() + 1);
        };

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
                    self.totalCount(result.totalCount);
                }
            });
        };

        //Default display list without any filtering
        self.getExecutionList();

        return self;
    }
    return { viewModel: viewModel, template: template };
});
