define(["knockout", "jquery", "text!components/mail-configuration/add.html", "validator"], function (ko, $, template, validator) {
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

                    self.smtpConfigurationPresent(true);
                }
            }
        });

        $.ajax("/api/mail/email-configuration", {
            type: "GET",
            contentType: "application/json",
            success: function(conf) {
                if (conf != null) {
                    self.from(conf.from);
                    self.bcc(conf.bcc);
                    self.replyTo(conf.replyTo);

                    self.emailConfigurationPresent(true);
                }
            }
        });

        $("#saveEmailConfigurationForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
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
                        self.messages(result.messages);
                        self.emailConfigurationPresent(true);
                    }
                });
            }

            return false;
        });

        $("#saveSmtpConfigurationForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                var smtpConfiguration = {
                    id: self.smtpConfigurationId(),
                    host: self.host(),
                    port: self.port(),
                    ssl: self.ssl() === "true",
                    username: self.username(),
                    password: self.password()
                };

                $.ajax("/api/mail/save-smtp-configuration", {
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

        $("#testEmailForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                $.ajax("/api/mail/" + self.toEmail() + "/email-configuration-test", {
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
