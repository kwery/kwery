define(["knockout", "jquery", "text!components/report/executing.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.executions = ko.observableArray();

        $.ajax("/api/job/executing", {
            type: "GET",
            contentType: "application/json",
            success: function(result) {
                self.executions(result);
            }
        });
        return self;
    }
    return {viewModel: viewModel, template: template};
});
