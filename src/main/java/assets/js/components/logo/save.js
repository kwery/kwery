define(["knockout", "jquery", "text!components/logo/save.html", "ajaxutil", "waitingmodal", "validator", "jstorage"],
    function (ko, $, template, ajaxUtil, waitingModal) {
    function ViewModel(params) {
        var self = this;

        self.displayMessage();

        self.logo = ko.observable("");
        self.id = ko.observable();

        self.logoAdded = ko.computed(function(){
            return self.logo() !== "";
        }, self);

        self.getLogo = function() {
            ajaxUtil.waitingAjax({
                url: "/api/report-email-configuration/get",
                type: "GET",
                contentType: "application/json",
                success: function(reportEmailConfiguration) {
                    if (reportEmailConfiguration !== null) {
                        self.logo(reportEmailConfiguration.logoUrl);
                        self.id(reportEmailConfiguration.id);
                    }
                }
            });
        };

        self.getLogo();

        return self;
    }

    ViewModel.prototype.submitForm = function() {
        var self = this;
        $.ajax({
            url: "/api/report-email-configuration/save",
            type: "POST",
            beforeSend: function(){
                waitingModal.show();
            },
            data: ko.toJSON({
                logoUrl: self.logo(),
                id: self.id()
            }),
            contentType: "application/json"
        }).done(function(actionResult){
            self.submitFormCb(actionResult);
            waitingModal.hide();
        });

        return false;
    };

    ViewModel.prototype.submitFormCb = function(actionResult){
        var self = this;
        if (actionResult.status === "success") {
            self.formSubmissionSuccessMessage();
            //_ is added to force loading of template
            document.location.href = "/#logo/save?_=" + new Date().getTime();
        }
    };

    ViewModel.prototype.formSubmissionSuccessMessage = function () {
        $.jStorage.set("logo:status", "success", {TTL: (10 * 60 * 1000)});
        $.jStorage.set("logo:message", ko.i18n('report.email.configuration.save.success'), {TTL: (10 * 60 * 1000)});
    };

    ViewModel.prototype.displayMessage = function () {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        var status = $.jStorage.get("logo:status", null);
        if (status !== null) {
            self.status(status);
            $.jStorage.deleteKey("logo:status");
        }

        var message = $.jStorage.get("logo:message", null);
        if (message !== null) {
            self.messages([message]);
            $.jStorage.deleteKey("logo:message");
        }
    };

    return { viewModel: ViewModel, template: template };
});
