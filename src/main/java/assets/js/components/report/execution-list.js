define(["knockout", "jquery", "text!components/report/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        //Pagination
        //No of results to show in the page
        var RESULT_COUNT = 10;
        var pageNumber = 0;
        var resultCount = RESULT_COUNT;

        if (params["?q"] !== undefined) {
            resultCount = params["?q"].resultCount;
            pageNumber = params["?q"].pageNumber;
        }

        self.pageNumber = ko.observable(pageNumber);
        self.pageNumber.extend({notify: 'always'});
        self.resultCount = ko.observable(resultCount);
        self.totalCount = ko.observable(0);

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

        self.previous = function() {
            self.pageNumber(self.pageNumber() - 1);
        };

        self.next = function() {
            self.pageNumber(self.pageNumber() + 1);
        };

        self.navigate = function () {
            window.location.href = "/#report/" + params.jobId + "/execution-list/?" +
                "pageNumber=" + self.pageNumber() +
                "&resultCount=" + self.resultCount();
        };

        self.pageNumber.subscribe(function(){
            self.navigate();
        });

        self.executions = ko.observableArray();
        $.ajax("/api/job/" + params.jobId + "/execution", {
            data: ko.toJSON({
                pageNumber: self.pageNumber(),
                resultCount: self.resultCount()
            }),
            type: "POST",
            contentType: "application/json",
            success: function(result) {
                ko.utils.arrayForEach(result.jobExecutionDtos, function(jobExecutionDto){
                    jobExecutionDto.executionResultLink = "/#report/" + params.jobId + "/execution/" + jobExecutionDto.executionId;
                });
                self.executions(result.jobExecutionDtos);
                self.totalCount(result.totalCount);
            }
        });
        return self;
    }
    return {viewModel: viewModel, template: template};
});
