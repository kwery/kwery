define(["knockout", "jquery", "text!components/user/sign-up.html", "ajaxutil", "validator", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        //An observable is passed from the parent template in case of edit user
        self.userId = params.userId;

        //Is this onboarding flow?
        if (params["?q"] && params["?q"].onboarding) {
            self.status("info");
            self.messages([ko.i18n("onboarding.user.add")]);
        }

        var update = false;

        if (self.userId) {
            update = true;
        }

        self.header = ko.observable();

        if (update) {
            self.header(ko.i18n('sign.up.update.header'));
        } else {
            self.header(ko.i18n('sign.up.add.header'));
        }

        self.firstName = ko.observable();
        self.middleName = ko.observable();
        self.lastName = ko.observable();
        self.password = ko.observable();
        self.confirmPassword = ko.observable();
        self.email = ko.observable();

        var originalPassword;
        if (update) {
            ajaxUtil.waitingAjax({
                url: "/api/user/" + self.userId(),
                type: "GET",
                contentType: "application/json",
                success: function(user) {
                    self.firstName(user.firstName);
                    self.middleName(user.middleName);
                    self.lastName(user.lastName);
                    self.email(user.email);
                    self.password(user.password);
                    self.confirmPassword(user.password);

                    originalPassword = self.password();
                }
            }, "getUser");
        }

        $("#signUpForm").validator({
            disable: false
        }).on("submit", function(e){
            if (!e.isDefaultPrevented()) {
                var user = {
                    firstName: self.firstName(),
                    middleName: self.middleName(),
                    lastName: self.lastName(),
                    password: self.password(),
                    email: self.email()
                };

                if (update) {
                    user.id = self.userId();
                }

                ajaxUtil.waitingAjax({
                    url: "/api/user/sign-up",
                    data: ko.toJSON(user),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        if (result.status === "success") {
                            if ($.jStorage.storageAvailable()) {
                                if (update) {
                                    $.jStorage.set("user:status", result.status, {TTL: (10 * 60 * 1000)});
                                    $.jStorage.set("user:messages", [ko.i18n('sign.up.update.success.message')], {TTL: (10 * 60 * 1000)});
                                    window.location.href = "#user/list";
                                } else {
                                    $.jStorage.set("us:status", result.status, {TTL: (10 * 60 * 1000)});
                                    $.jStorage.set("us:messages", [ko.i18n('sign.up.success.message')], {TTL: (10 * 60 * 1000)});
                                    window.location.href = "#user/login";
                                }
                            } else {
                                throw new Error("Not enough space available to store result in browser");
                            }
                        } else {
                            self.status(result.status);
                            self.messages([ko.i18n('sign.up.failure.message', {"0": self.email()})]);
                        }
                    }
                }, "userSignUp");
            }

            return false;
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
