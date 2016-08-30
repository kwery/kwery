define(["knockout", "jquery", "text!components/datasource/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();
        self.url = ko.observable();
        self.label = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        var validate = $('form').validate({
            debug: true,
            messages: {
                username: {
                    required: ko.i18n("username.validation"),
                    minlength: ko.i18n("username.validation")
                },
                url: {
                    required: ko.i18n("url.validation"),
                    minlength: ko.i18n("url.validation")
                },
                label: {
                    required: ko.i18n("label.validation"),
                    minlength: ko.i18n("label.validation")
                }
            }
        });

        self.submit = function(formElem) {
            if ($(formElem).valid()) {
                $.ajax("/api/datasource/add-datasource", {
                    data: ko.toJSON({
                        url: self.url,
                        username: self.username,
                        password: self.password,
                        label: self.label,
                        type: "MYSQL"
                    }),
                    type: "post", contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);

                        var messages = result.messages;
                        var fieldMessages = result.fieldMessages;

                        self.messages([]);
                        if (messages != null) {
                            ko.utils.arrayPushAll(self.messages, result.messages)
                        }

                        if (fieldMessages != null) {
                            ko.utils.arrayForEach(["url", "username", "label"], function(elem){
                                if (elem in fieldMessages) {
                                    ko.utils.arrayPushAll(self.messages, fieldMessages[elem])
                                }
                            });
                        }
                    }
                });
            }
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
