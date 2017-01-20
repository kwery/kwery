define(["knockout", "jquery", "text!components/report/list.html", "ajaxutil", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        //To show save messages
        var status = $.jStorage.get("report:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("report:status");
        }

        var messages = $.jStorage.get("report:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("report:messages");
        }

        self.reports = ko.observableArray([]);

        ajaxUtil.waitingAjax({
            url: "/api/job/list",
            type: "POST",
            contentType: "application/json",
            data: ko.toJSON({
                jobLabelId: 0
            }),
            success: function (result) {
                ko.utils.arrayForEach(result, function(jobModelHackDto){
                    jobModelHackDto.jobModel.executionLink = "/#report/" + jobModelHackDto.jobModel.id + "/execution-list";
                    jobModelHackDto.jobModel.reportLink = "/#report/" + jobModelHackDto.jobModel.id;
                    self.reports.push(jobModelHackDto.jobModel);
                });
            }
        });

        self.executeReport = function(report) {
            ajaxUtil.waitingAjax({
                url: "/api/job/" + report.id + "/execute",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages([ko.i18n("report.list.execute.now.success")]);
                }
            })
        };

        self.deleteReport = function(report) {
            ajaxUtil.waitingAjax({
                url: "/api/job/" + report.id + "/delete",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    if (result.status === "success") {
                        self.status(result.status);
                        self.messages([ko.i18n("report.list.delete.success")]);
                        self.reports.remove(report);
                    } else {
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                }
            })
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
