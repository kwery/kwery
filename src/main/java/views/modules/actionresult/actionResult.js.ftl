define(["knockout", "jquery", "text!${templatePath}", "knockout-jqueryui/dialog"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;
        self.nextAction = params.nextAction;
        self.nextActionName = params.nextActionName;
        self.onSuccessShowDialog = params.onSuccessShowDialog;

        self.showSuccess = ko.computed(function(){
            return (self.onSuccessShowDialog === undefined || self.onSuccessShowDialog === false) && self.status() === "success";
        }, self);

        self.showFailure = ko.computed(function(){
            return self.status() === "failure";
        }, self);

        self.isOpen = ko.computed(function(){
            return (self.onSuccessShowDialog !== undefined && self.onSuccessShowDialog === true) && self.status() === "success";
        }, self);

        return self;
    }
    return { viewModel: viewModel, template: template };
});
