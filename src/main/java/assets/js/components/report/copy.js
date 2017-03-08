define(["knockout", "text!components/report/copy.html"], function (ko, template) {
    function viewModel(params) {
        var self = this;
        var reportId = params.reportId;

        self.isCopy = ko.observable(true);
        self.reportId = ko.observable(reportId);

        return self;
    }
    return {viewModel: viewModel, template: template};
});
