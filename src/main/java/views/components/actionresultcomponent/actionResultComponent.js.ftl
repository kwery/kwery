define(["knockout", "jquery", "text!/component/actionresultcomponent.html"], function (ko, $, actionComponentTemplate) {
    function viewModel(params) {
        var self = this;
        self.successMessage = params.successMessage;
        self.failureMessage = params.failureMessage;
        return self;
    }
    return { viewModel: viewModel, template: actionComponentTemplate };
});
