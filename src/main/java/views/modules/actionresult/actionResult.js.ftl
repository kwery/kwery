define(["knockout", "jquery", "text!${templatePath}"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;

        self.showSuccess = ko.computed(function(){
            return self.status() === "success";
        }, self);

        self.showFailure = ko.computed(function(){
            return self.status() === "failure";
        }, self);

        return self;
    }
    return { viewModel: viewModel, template: template };
});
