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
                ko.utils.arrayForEach(result, function(jobModelHackDto){
                    jobModelHackDto.jobModel.executionLink = "/#report/" + jobModelHackDto.jobModel.id + "/execution-list";
                    jobModelHackDto.jobModel.reportLink = "/#report/" + jobModelHackDto.jobModel.id;
                    self.reports.push(jobModelHackDto.jobModel);
                });
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

        self.deleteReport = function(report) {
            $.ajax({
                url: "/api/job/" + report.id + "/delete",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages([ko.i18n("report.list.delete.success")]);
                    self.reports.remove(report);
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
