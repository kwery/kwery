define(["knockout", "jquery", "text!components/user/admin/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.submit = function() {
            $.ajax("/api/user/add-admin-user", {
                data: ko.toJSON({username: self.username, password: self.password}),
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
                        ko.utils.arrayForEach(["username", "password"], function(elem){
                            if (elem in fieldMessages) {
                                ko.utils.arrayPushAll(self.messages, fieldMessages[elem])
                            }
                        });
                    }

                    self.nextActionName = ko.i18n("admin.user.addition.next.action");
                    self.nextAction = "#user/login";
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
