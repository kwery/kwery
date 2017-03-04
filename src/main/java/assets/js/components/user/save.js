define(["knockout", "text!components/user/save.html"], function (ko, template) {
    function viewModel(params) {
        var self = this;
        self.userId = ko.observable(params.userId);
        return self;
    }
    return {viewModel: viewModel, template: template};
});
