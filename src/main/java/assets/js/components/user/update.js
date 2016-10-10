define(["knockout", "jquery", "text!components/user/update.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable("");
        self.password = ko.observable("");

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        $.ajax("/api/user/" + params.userId, {
            type: "GET",
            contentType: "appliction/json",
            success: function(result) {
                self.username(result.username);
                self.password(result.password);
            }
        });

        var validate = $('form').validate({
            debug: true,
            messages: {
                password: {
                    required: ko.i18n("password.validation"),
                    minlength: ko.i18n("password.validation")
                }
            }
        });

        self.submit = function(formElem) {
            if ($(formElem).valid()) {
                $.ajax("/api/user/add-admin-user", {
                    data: ko.toJSON({
                        username: self.username,
                        password: self.password,
                        id: params.userId
                    }),
                    type: "post", contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);
                        self.messages(result.messages || []);
                    }
                });
            }
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
