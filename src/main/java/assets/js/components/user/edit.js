define(["knockout", "jquery", "text!components/user/edit.html", "ajaxutil", "validator"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.userId = params.userId;

        self.firstName = ko.observable();
        self.middleName = ko.observable();
        self.lastName = ko.observable();
        self.password = ko.observable();
        self.newPassword = ko.observable();
        self.confirmPassword = ko.observable();
        self.email = ko.observable();
        self.superUser = ko.observable();

        ajaxUtil.waitingAjax({
            url: "/api/user/" + self.userId,
            type: "GET",
            contentType: "application/json",
            success: function(user) {
                self.firstName(user.firstName);
                self.middleName(user.middleName);
                self.lastName(user.lastName);
                self.email(user.email);
                self.superUser(user.superUser);
                self.password(user.password);
            }
        }, "getUser");

        $("#resetForm").validator({
            disable: false
        }).on("submit", function(e){
            if (!e.isDefaultPrevented()) {
                var user = {
                    firstName: self.firstName(),
                    middleName: self.middleName(),
                    lastName: self.lastName(),
                    password: self.newPassword(),
                    email: self.email(),
                    id: self.userId
                };

                ajaxUtil.waitingAjax({
                    url: "/api/user/sign-up",
                    data: ko.toJSON(user),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        if (result.status === "success") {
                            self.status(result.status);
                            self.messages([ko.i18n('user.edit.password.success.message')]);
                        }
                    }
                }, "updateUser");
            }

            return false;
        });

        self.superUserSave = function() {
            var user = {
                firstName: self.firstName(),
                middleName: self.middleName(),
                lastName: self.lastName(),
                password: self.password(),
                email: self.email(),
                id: self.userId,
                superUser: self.superUser()
            };

            ajaxUtil.waitingAjax({
                url: "/api/user/sign-up",
                data: ko.toJSON(user),
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    if (result.status === "success") {
                        self.messages([ko.i18n('user.edit.superuser.success.message')]);
                    } else {
                        self.messages([ko.i18n('user.edit.superuser.failure.message')]);
                    }
                }
            });

            return false;
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
