define(["knockout", "jquery", "text!components/user/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.users = ko.observableArray([]);

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        $.ajax({
            url: "/api/user/list",
            type: "GET",
            contentType: "application/json",
            success: function(result){
                self.users(result);
            }
        });

        self.delete = function(toDeleteUser) {
            $.ajax({
                url: "/api/user/delete/" + toDeleteUser.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages || []);

                    if (result.success) {
                        self.users.remove(toDeleteUser);
                    }
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});

