define(["knockout", "text!/component/actionresultdialogcomponent.html", "knockout-jqueryui/dialog"], function (ko, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;
        self.nextAction = params.nextAction;
        self.nextActionName = params.nextActionName;
        self.isOpen = params.isOpen;
        return self;
    }
    return {viewModel: viewModel, template: template};
});
