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

        self.delete = function(user) {
            $.ajax({
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

        return self;
    }
    return { viewModel: viewModel, template: template };
});

