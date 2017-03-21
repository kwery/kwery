define(["knockout", "jquery", "text!components/user/list.html", "ajaxutil", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        //To show save messages
        var status = $.jStorage.get("user:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("user:status");
        }

        var messages = $.jStorage.get("user:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("user:messages");
        }

        self.users = ko.observableArray([]);

        ajaxUtil.waitingAjax({
            url: "/api/user/list",
            type: "GET",
            contentType: "application/json",
            success: function(result){
                ko.utils.arrayForEach(result, function (user) {
                    user.editLink = "/#user/" + user.id + "/edit";
                });
                self.users(result);
            }
        });

        self.delete = function(user) {
            ajaxUtil.waitingAjax({
                url: "/api/user/delete/" + user.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages || []);

                    if (result.status === "success") {
                        self.users.remove(user);
                    }
                }
            });
        };

        $('[data-toggle="tooltip"]').tooltip();

        return self;
    }
    return { viewModel: viewModel, template: template };
});

