define(["knockout", "text!/component/actionresultdialogcomponent.html", "knockout-jqueryui/dialog"], function (ko, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;
        self.nextAction = params.nextAction;
        self.nextActionName = params.nextActionName;

        self.isOpen = ko.computed(function(){
            return self.status() === "success";
        }, self);

        self.showFailure = ko.computed(function(){
            return self.status() === "failure";
        }, self);

        return self;
    }
    return {viewModel: viewModel, template: template};
});
