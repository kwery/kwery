define(["knockout", "jquery", "text!components/report/execution-list.html", "moment", "datetimepicker"], function (ko, $, template, moment) {
    function viewModel(params) {
        //TODO - Optimize below
        var DISPLAY_DATE_FORMAT = "ddd MMM DD YYYY HH:mm";
        ko.bindingHandlers.dateTimePicker = {
            init: function (element, valueAccessor, allBindingsAccessor) {
                //initialize datepicker with some optional options
                var options = allBindingsAccessor().dateTimePickerOptions || {};
                $(element).datetimepicker({
                    format: DISPLAY_DATE_FORMAT
                });

                //when a user changes the date, update the view model
                ko.utils.registerEventHandler(element, "dp.change", function (event) {
                    var value = valueAccessor();
                    if (ko.isObservable(value)) {
                        if (event.date != null && !(event.date instanceof Date)) {
                            value(event.date.format(DISPLAY_DATE_FORMAT));
                        } else {
                            value(event.date);
                        }
                    }
                });

                ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                    var picker = $(element).data("DateTimePicker");
                    if (picker) {
                        picker.destroy();
                    }
                });
            },
            update: function (element, valueAccessor, allBindings, viewModel, bindingContext) {
                var picker = $(element).data("DateTimePicker");
                //when the view model is updated, update the widget
                if (picker) {
                    var koDate = ko.utils.unwrapObservable(valueAccessor());
                    picker.date(new moment(koDate, DISPLAY_DATE_FORMAT));
                }
            }
        };

        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        //Pagination
        //No of results to show in the page
        var RESULT_COUNT = 10;
        var pageNumber = 0;
        var resultCount = RESULT_COUNT;
        var executionStartStart = "";
        var executionStartEnd = "";

        if (params["?q"] !== undefined) {
            resultCount = params["?q"].resultCount;
            pageNumber = params["?q"].pageNumber;
            executionStartStart = params["?q"].executionStartStart;
            executionStartEnd = params["?q"].executionStartEnd;
        }

        self.pageNumber = ko.observable(pageNumber);
        self.pageNumber.extend({notify: 'always'});
        self.resultCount = ko.observable(resultCount);
        self.totalCount = ko.observable(0);
        self.executionStartStart = ko.observable(executionStartStart);
        self.executionStartEnd = ko.observable(executionStartEnd);

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
                "&resultCount=" + self.resultCount() +
                "&executionStartStart=" + self.executionStartStart() +
                "&executionStartEnd=" + self.executionStartEnd();
        };

        self.pageNumber.subscribe(function(){
            self.navigate();
        });

        self.filter = function() {
            self.updateExecutions();
        };

        self.executions = ko.observableArray();
        self.updateExecutions = function() {
            $.ajax("/api/job/" + params.jobId + "/execution", {
                data: ko.toJSON({
                    pageNumber: self.pageNumber(),
                    resultCount: self.resultCount(),
                    executionStartStart: self.executionStartStart(),
                    executionStartEnd: self.executionStartEnd()
                }),
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    if (result.status !== undefined) {
                        self.status(result.status);
                        self.messages(result.messages);
                        self.executions([]);
                    } else {
                        ko.utils.arrayForEach(result.jobExecutionDtos, function(jobExecutionDto){
                            jobExecutionDto.executionResultLink = "/#report/" + params.jobId + "/execution/" + jobExecutionDto.executionId;
                        });
                        self.executions(result.jobExecutionDtos);
                        self.totalCount(result.totalCount);
                    }
                }
            });

        };
        self.updateExecutions();

        return self;
    }
    return {viewModel: viewModel, template: template};
});
