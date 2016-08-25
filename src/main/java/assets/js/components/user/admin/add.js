define(["knockout", "jquery", "text!js/components/user/admin/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable();

        self.status = ko.observable("");
        self.message = ko.observable("");

        self.submit = function() {
            $.ajax("/api/user/add-admin-user", {
                data: ko.toJSON({username: self.username, password: self.password}),
                type: "post", contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.message(result.message);

                    self.nextActionName = ko.i18n("admin.user.addition.next.action");
                    self.nextAction = "#user/login";
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
