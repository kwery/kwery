define(["knockout", "jquery", "text!/component/actionresultcomponent.html"], function (ko, $, actionComponentTemplate) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.message = params.message;
        return self;
    }
    return { viewModel: viewModel, template: actionComponentTemplate };
});
