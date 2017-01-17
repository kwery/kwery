define(["knockout", "jquery", "text!components/user/admin/add.html", "validator"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable("");
        self.password = ko.observable("");

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        $("#addUserForm").validator({
            disable: false
        }).on("submit", function(e) {
            if (!e.isDefaultPrevented()) {
                $.ajax("/api/user/add-admin-user", {
                    data: ko.toJSON({username: self.username(), password: self.password()}),
                    type: "post", contentType: "application/json",
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
