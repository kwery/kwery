define(["knockout", "jquery", "text!components/action-result.html", "knockout-jqueryui/dialog"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;
        self.nextAction = params.nextAction;
        self.nextActionName = params.nextActionName;
        self.onSuccessShowDialog = params.onSuccessShowDialog;

        self.showSuccess = ko.computed(function(){
            var onSuccessShowDialog = false;

            if (self.onSuccessShowDialog !== undefined) {
                if (ko.isObservable(self.onSuccessShowDialog)) {
                    onSuccessShowDialog = self.onSuccessShowDialog();
                } else {
                    onSuccessShowDialog = self.onSuccessShowDialog;
                }
            }

            return (self.onSuccessShowDialog === undefined || onSuccessShowDialog === false) && self.status() === "success";
        }, self);

        self.showFailure = ko.computed(function(){
            return self.status() === "failure";
        }, self);

        self.isOpen = ko.computed(function(){
            var onSuccessShowDialog = false;

            if (self.onSuccessShowDialog !== undefined) {
                if (ko.isObservable(self.onSuccessShowDialog)) {
                    onSuccessShowDialog = self.onSuccessShowDialog();
                } else {
                    onSuccessShowDialog = self.onSuccessShowDialog;
                }
            }

            return (self.onSuccessShowDialog !== undefined &&  onSuccessShowDialog === true) && self.status() === "success";
        }, self);

        return self;
    }
    return { viewModel: viewModel, template: template };
});
