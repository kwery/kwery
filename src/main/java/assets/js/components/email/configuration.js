define(["knockout", "jquery", "text!components/email/configuration.html", "ajaxutil", "waitingmodal", "validator"], function (ko, $, template, ajaxUtil, waitingModal) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.smtpConfigurationId = ko.observable();
        self.host = ko.observable();
        self.port = ko.observable();
        self.ssl = ko.observable();
        self.username = ko.observable();
        self.password = ko.observable();

        self.emailConfigurationId = ko.observable();
        self.from = ko.observable();
        self.bcc = ko.observable();
        self.replyTo = ko.observable();

        self.smtpConfigurationPresent = ko.observable(false);
        self.emailConfigurationPresent = ko.observable(false);

        self.toEmail = ko.observable();
        self.useLocalSetting = ko.observable(false);

        self.useLocalSetting.subscribe(function(val){
            if (val) {
                self.host("localhost");
                self.port(25);

                self.username("");
                self.password("");
                self.ssl("");

                $("#sslTrue").attr("data-validate", false);
                $("#sslFalse").attr("data-validate", false);
                $("#username").attr("data-validate", false);
                $("#password").attr("data-validate", false);
            } else {
                $("#sslTrue").attr("data-validate", true);
                $("#sslFalse").attr("data-validate", true);
                $("#username").attr("data-validate", true);
                $("#password").attr("data-validate", true);
            }

            //To remove any existing validation messages associated with parent label
            $("#saveSmtpConfigurationForm").validator("destroy");
            self.validateSmtpConfigurationForm();
            self.refreshValidation();
            //Validation messages associated with actually invalid elements should still be shown
            $("#saveSmtpConfigurationForm").validator("validate");
        });

        waitingModal.show();

        $.when(
            $.ajax("/api/mail/smtp-configuration", {
                type: "GET",
                contentType: "application/json",
                success: function(conf) {
                    if (conf != null) {
                        self.smtpConfigurationId(conf.id);
                        self.host(conf.host);
                        self.port(conf.port);
                        self.ssl(conf.ssl.toString());
                        self.username(conf.username);
                        self.password(conf.password);
                        self.useLocalSetting(conf.useLocalSetting);

                        self.smtpConfigurationPresent(true);
                    }
                }
            }),
            $.ajax("/api/mail/email-configuration", {
                type: "GET",
                contentType: "application/json",
                success: function(conf) {
                    if (conf != null) {
                        self.emailConfigurationId(conf.id);
                        self.from(conf.from);
                        self.bcc(conf.bcc);
                        self.replyTo(conf.replyTo);

                        self.emailConfigurationPresent(true);
                    }
                }
            })
        ).always(function(){
            waitingModal.hide();

            //We want this message to be shown only when the page loads, not after any actions
            //This should be shown only if either one of the configurations are missing, not when both are missing
            if (self.emailConfigurationPresent() || self.smtpConfigurationPresent()) {
                if (!(self.emailConfigurationPresent() && self.smtpConfigurationPresent())) {
                    self.status("failure");
                    if (!self.smtpConfigurationPresent()) {
                        self.messages([ko.i18n("email.configuration.smtp.missing")]);
                    }

                    if (!self.emailConfigurationPresent()) {
                        self.messages([ko.i18n("email.configuration.sender.details.missing")]);
                    }
                }
            }
        });

        self.validateSmtpConfigurationForm = function() {
            $("#saveSmtpConfigurationForm").validator({disable: false}).on("submit", function (e) {
                if (e.isDefaultPrevented()) {
                    // handle the invalid form...
                } else {
                    var smtpConfiguration = {
                        id: self.smtpConfigurationId(),
                        host: self.host(),
                        port: self.port(),
                        useLocalSetting: self.useLocalSetting()
                    };

                    if (!self.useLocalSetting()) {
                        smtpConfiguration.ssl = self.ssl() === "true";
                        smtpConfiguration.username = self.username();
                        smtpConfiguration.password = self.password();
                    }

                    ajaxUtil.waitingAjax({
                        url: "/api/mail/save-smtp-configuration",
                        type: "POST",
                        data: ko.toJSON(smtpConfiguration),
                        contentType: "application/json",
                        success: function(result) {
                            self.status(result.status);
                            self.messages(result.messages);
                            self.smtpConfigurationPresent(true);
                        }
                    });
                }
                return false;
            });
        };

        self.validateSmtpConfigurationForm();

        self.refreshValidation = function() {
            $("#saveSmtpConfigurationForm").validator("update");
        };

        $("#saveEmailConfigurationForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                var conf = {
                    id: self.emailConfigurationId(),
                    from: self.from(),
                    bcc: self.bcc(),
                    replyTo: self.replyTo()
                };

                ajaxUtil.waitingAjax({
                    "url": "/api/mail/save-email-configuration",
                    type: "POST",
                    data: ko.toJSON(conf),
                    contentType: "application/json",
                    beforeSend: function(){
                        waitingModal.show(ko.i18n("progress.indicator.msg.saving"));
                    },
                    success: function(result) {
                        self.status(result.status);
                        self.messages(result.messages);
                        self.emailConfigurationPresent(true);
                    }
                })
            }

            return false;
        });

        $("#testEmailForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                ajaxUtil.waitingAjax({
                    url: "/api/mail/" + self.toEmail() + "/email-configuration-test",
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                });
            }

            return false;
        });

        self.configurationsPresent = ko.computed(function(){
            return self.smtpConfigurationPresent() && self.emailConfigurationPresent();
        }, self);


        return self;
    }
    return { viewModel: viewModel, template: template };
});
