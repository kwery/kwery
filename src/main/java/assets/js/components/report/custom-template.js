define(["knockout", "text!components/report/custom-template.html", "ajaxutil", "waitingmodal", "validator", "jstorage"], function (ko, template, ajaxUtil, waitingModal) {
    function viewModel(params) {
        var self = this;
        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        var status = $.jStorage.get("customTemplate:status", null);
        if (status !== null) {
            self.status(status);
            $.jStorage.deleteKey("customTemplate:status");
        }

        var message = $.jStorage.get("customTemplate:message", null);
        if (message !== null) {
            self.messages([message]);
            $.jStorage.deleteKey("customTemplate:message");
        }

        self.apiUrl = ko.observable("/api/job/" + params.jobId + "/save-custom-template");

        self.templateContent = ko.observable("");
        self.templatePresent = ko.computed(function(){
            return self.templateContent() !== "";
        }, self);

        ajaxUtil.waitingAjax({
            url: "/api/job/" + params.jobId + "/get-custom-template",
            type: "GET",
            contentType: "application/json",
            success: function(response) {
                self.templateContent(response.content);
            }
        }, "customTemplateGet");

        $("#customTemplateForm").validator({
            disable: false
        }).on("submit", function(e){
            if (!e.isDefaultPrevented()) {
                var iFrame = $('<iframe name="postFrame" id="postFrame" style="display: none"></iframe>');
                $("body").append(iFrame);

                var form = $('#customTemplateForm');
                $("#customTemplateForm").off("submit");
                waitingModal.show(undefined, "customTemplateSave");
                form.submit();

                $("#postFrame").on("load", function() {
                    var actionResult = $.parseJSON($('#postFrame').contents().find('body').html());
                    if (actionResult.status === "success") {
                        waitingModal.hide("customTemplateSave");
                        $.jStorage.set("customTemplate:status", "success", {TTL: (10 * 60 * 1000)});
                        $.jStorage.set("customTemplate:message", ko.i18n('template.edit.save.success'), {TTL: (10 * 60 * 1000)});
                        document.location.href = "/#report/" + params.jobId + "/custom-template" + "?_=" + new Date().getTime();
                    }
                });
            }

            return false;
        });

        $("#delete").on("click", function(){
            ajaxUtil.waitingAjax({
                url: "/api/job/" + params.jobId + "/delete-custom-template",
                type: "POST",
                contentType: "application/json",
                success: function(response) {
                    if (response.status === "success") {
                        $.jStorage.set("customTemplate:status", "success", {TTL: (10 * 60 * 1000)});
                        $.jStorage.set("customTemplate:message", ko.i18n('template.edit.delete.success'), {TTL: (10 * 60 * 1000)});
                        document.location.href = "/#report/" + params.jobId + "/custom-template" + "?_=" + new Date().getTime();
                    }
                }
            }, "deleteTemplateGet");
        });

        return self;
    }
    return {viewModel: viewModel, template: template};
});
