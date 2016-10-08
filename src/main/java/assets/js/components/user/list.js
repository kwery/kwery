define(["knockout", "jquery", "text!components/user/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.users = ko.observableArray([]);

        $.ajax({
            url: "/api/user/list",
            type: "GET",
            contentType: "application/json",
            success: function(result){
                self.users(result);
            }
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});

