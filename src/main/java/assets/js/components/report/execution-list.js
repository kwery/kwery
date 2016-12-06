define(["knockout", "jquery", "text!components/report/execution-list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.executions = ko.observableArray();

        $.ajax("/api/job/" + params.jobId + "/execution", {
            type: "GET",
            contentType: "application/json",
            success: function(result) {
                ko.utils.arrayForEach(result, function(executionResult){
                    executionResult.executionResultLink = "/#report/" + params.jobId + "/execution/" + executionResult.executionId;
                });
                self.executions(result);
            }
        });
        return self;
    }
    return {viewModel: viewModel, template: template};
});
