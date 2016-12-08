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

        self.executeReport = function(report) {
            $.ajax({
                url: "/api/job/" + report.id + "/execute",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages([ko.i18n("report.list.execute.now.success")]);
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
