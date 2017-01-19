define(["knockout", "jquery", "text!components/user/list.html", "ajaxutil"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.users = ko.observableArray([]);

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        ajaxUtil.waitingAjax({
            url: "/api/user/list",
            type: "GET",
            contentType: "application/json",
            success: function(result){
                ko.utils.arrayForEach(result, function (user) {
                    user.updateLink = "/#user/" + user.id;
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

