define(["knockout", "jquery", "text!components/user/add.html", "ajaxutil", "validator"], function (ko, $, template, ajaxUtil) {
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
            });
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
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                });

                return false;
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
