define(["knockout", "jquery", "text!components/mail-configuration/add.html"], function (ko, $, template) {
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

        self.saveSmtpConfiguration = function(){
            var e = {
                id: self.smtpConfigurationId(),
                host: self.host(),
                port: self.port(),
                ssl: self.ssl() === "true",
                username: self.username(),
                password: self.password()
            };

            $.ajax("/api/mail/save-smtp-configuration", {
                type: "POST",
                data: ko.toJSON(e),
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    ko.utils.arrayPushAll(self.messages, result.messages)
                }
            });
        };

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
                    self.password(conf.password)
                }
            }
        });

        self.saveEmailConfiguration = function() {
            var conf = {
                from: self.from(),
                bcc: self.bcc(),
                replyTo: self.replyTo()
            };

            $.ajax("/api/mail/save-email-configuration", {
                type: "POST",
                data: ko.toJSON(conf),
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    ko.utils.arrayPushAll(self.messages, result.messages)
                }
            });
        };

        $.ajax("/api/mail/email-configuration", {
            type: "GET",
            contentType: "application/json",
            success: function(conf) {
                if (conf != null) {
                    self.from(conf.from);
                    self.bcc(conf.bcc);
                    self.replyTo(conf.replyTo);
                }
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
