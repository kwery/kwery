define(["knockout", "jquery", "text!components/report/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.reports = ko.observableArray([]);
        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        $.ajax({
            url: "/api/job/list",
            type: "GET",
            contentType: "application/json",
            success: function (result) {
                ko.utils.arrayForEach(result, function(report){
                    report.executionLink = "/#report/" + report.id + "/execution-list"
                });
                self.reports(result);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
