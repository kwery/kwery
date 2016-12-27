define(["knockout", "jquery", "text!components/report/executing.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.executions = ko.observableArray();

        $.ajax("/api/job/executing", {
            type: "GET",
            contentType: "application/json",
            success: function(result) {
                self.executions(result);
            }
        });

        self.stopJobExecution = function(execution) {
            $.ajax("/api/job/execution/stop/" + execution.executionId, {
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    if (result.status === 'success') {
                        self.messages.push(ko.i18n('report.job.executing.stop.success'));
                        self.executions.remove(execution);
                    } else {
                        self.messages([ko.i18n('report.job.executing.stop.failure')]);
                    }
                    self.status(result.status);
                }
            });
        };

        return self;
    }
    return {viewModel: viewModel, template: template};
});
