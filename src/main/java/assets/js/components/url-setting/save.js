define(["knockout", "jquery", "text!components/url-setting/save.html", "ajaxutil", "validator"], function (ko, $, template, ajaxUtil) {
    function viewModel() {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.port = ko.observable();
        self.domain = ko.observable();
        self.scheme = ko.observable();
        self.id = ko.observable();

        ajaxUtil.waitingAjax({
            url: "/api/url-setting",
            type: "GET",
            contentType: "application/json",
            success: function(urlSetting) {
                if (urlSetting !== null) {
                    self.port(urlSetting.port);
                    self.domain(urlSetting.domain);
                    self.scheme(urlSetting.scheme);
                    self.id(urlSetting.id);
                } else {
                    self.port(window.location.port ? window.location.port : 80);
                    self.domain(window.location.hostname);
                    self.scheme(window.location.protocol);
                }
            }
        });

        $("#urlSettingForm").validator({disable: false}).on("submit", function (e) {
            if (!e.isDefaultPrevented()) {
                ajaxUtil.waitingAjax({
                    url: "/api/url-setting/save",
                    type: "POST",
                    data: ko.toJSON({
                        scheme: self.scheme(),
                        domain: self.domain(),
                        port: self.port(),
                        id: self.id()
                    }),
                    contentType: "application/json",
                    success: function(actionResult) {
                        if (actionResult.status === "success") {
                            self.status(actionResult.status);
                            self.messages([ko.i18n("url.setting.save.success")]);
                        }
                    }
                });
            }

            return false;
        });

        return self;
    }
    return {viewModel: viewModel, template: template};
});
