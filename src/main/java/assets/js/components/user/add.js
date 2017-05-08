define(["knockout", "jquery", "text!components/user/add.html", "ajaxutil", "validator", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.username = ko.observable();
        self.password = ko.observable();

        self.isUpdate = ko.observable(false);

        var isUpdate = false;

        if (params.userId !== undefined) {
            isUpdate = true;
        }

        if (isUpdate) {
            self.isUpdate(true);

            ajaxUtil.waitingAjax({
                url: "/api/user/" + params.userId,
                type: "GET",
                contentType: "appliction/json",
                success: function(result) {
                    self.username(result.username);
                    self.password(result.password);
                }
            }, "getUser");
        }

        $("#addUserForm").validator({
            disable: false
        }).on("submit", function(e) {
            if (!e.isDefaultPrevented()) {
                var data = {
                    username: self.username(),
                    password: self.password()
                };

                if (isUpdate) {
                    data.id = params.userId;
                }

                ajaxUtil.waitingAjax({
                    url: "/api/user/add-admin-user",
                    data: ko.toJSON(data),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        if (result.status === "success") {
                            if ($.jStorage.storageAvailable()) {
                                $.jStorage.set("user:status", result.status, {TTL: (10 * 60 * 1000)});
                                $.jStorage.set("user:messages", result.messages, {TTL: (10 * 60 * 1000)});
                                window.location.href = "#user/list";
                            } else {
                                throw new Error("Not enough space available to store result in browser");
                            }
                        } else {
                            self.status(result.status);
                            self.messages(result.messages);
                        }
                    }
                }, "addUser");

                return false;
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
