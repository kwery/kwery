define(["knockout", "jquery", "text!components/sql-query/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        //Pagination
        //No of results to show in the page
        var RESULT_COUNT = 4;

        var pageNumber = 0;
        var resultCount = RESULT_COUNT;
        var executionStartStart = "";
        var executionStartEnd = "";
        var executionEndStart = "";
        var executionEndEnd = "";
        var statuses = "";
        //By default filter section is collapsed
        var collapseFilter = true;

        if (params["?q"] !== undefined) {
            resultCount = params["?q"].resultCount;
            pageNumber = params["?q"].pageNumber;
            executionStartStart = params["?q"].executionStartStart;
            executionStartEnd = params["?q"].executionStartEnd;
            executionEndStart = params["?q"].executionEndStart;
            executionEndEnd = params["?q"].executionEndEnd;
            statuses = params["?q"].statuses;
            collapseFilter = params["?q"].collapseFilter;
        }

        //Filter form parameters
        self.executionStartStart = ko.observable(executionStartStart);
        self.executionStartEnd = ko.observable(executionStartEnd);
        self.executionEndStart = ko.observable(executionEndStart);
        self.executionEndEnd = ko.observable(executionEndEnd);
        self.statuses = ko.observableArray(statuses !== "" ? statuses.split(",") : []);

        self.pageNumber = ko.observable(pageNumber);
        self.pageNumber.extend({notify: 'always'});

        self.resultCount = ko.observable(resultCount);

        self.totalCount = ko.observable(0);

        self.collapseFilter = ko.observable(collapseFilter);

        self.nextStatus = ko.pureComputed(function () {
            if (self.totalCount() <= ((self.pageNumber() + 1) * self.resultCount())) {
                return "disabled";
            }
            return "";
        });

        self.previousStatus = ko.pureComputed(function(){
            if (self.pageNumber() <= 0) {
                return "disabled";
            }
            return "";
        }, self);

        //Can be changed only on the first page, cannot be changed in between pagination
        self.canChangeResultCount = ko.computed(function(){
            return self.pageNumber() == 0;
        }, self);

        self.navigate = function () {
            window.location.href = "/#sql-query/" + params.sqlQueryId + "/execution-list/?" +
                "executionStartStart=" + self.executionStartStart() +
                "&executionStartEnd=" + self.executionStartEnd() +
                "&executionEndStart=" + self.executionEndStart() +
                "&executionEndEnd=" + self.executionEndEnd() +
                "&statuses=" + self.statuses().join(",") +
                "&pageNumber=" + self.pageNumber() +
                "&collapseFilter=" + self.collapseFilter() +
                "&resultCount=" + self.resultCount();
        };

        self.pageNumber.subscribe(function(){
            self.navigate();
        });

        self.executions = ko.observableArray([]);
        self.sqlQuery = ko.observable("");

        self.collapseCss = ko.computed(function(){
            return self.collapseFilter() ? "collapse" : "collapse in";
        }, self);

        self.submit = function (formElem) {
            //Only if filter is explicitly clicked then in next pages filter section should be open
            self.collapseFilter(false);
            self.pageNumber(0);
        };

        self.previous = function() {
            self.pageNumber(self.pageNumber() - 1);
        };


        self.next = function() {
            self.pageNumber(self.pageNumber() + 1);
        };

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
                var modified = [];

                ko.utils.arrayPushAll(
                    self.executions(),
                    result.sqlQueryExecutionDtos.map(function(obj){
                        if (obj.status === "SUCCESS") {
                            obj.resultLink = "/#sql-query/" + params.sqlQueryId + "/execution/" + obj.sqlQueryExecutionId;
                            obj.showLink = ko.observable(true);
                        } else {
                            obj.showLink = ko.observable(false);
                            obj.resultLink = "";
                        }
                        return obj;
                    })
                );

                self.executions(result.sqlQueryExecutionDtos);
                self.sqlQuery(result.sqlQuery);
                self.totalCount(result.totalCount);
            }
        });

        $('#executionList').on('hidden.bs.collapse', function () {
            self.collapseFilter(true);
        });

        $('#executionList').on('shown.bs.collapse', function () {
            self.collapseFilter(false);
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
