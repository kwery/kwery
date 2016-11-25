define(["knockout", "jquery", "text!components/panel-add-link.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.url = ko.observable(params.url);
        self.tooltipMessage = ko.observable(ko.i18n(params.tooltipMessageKey));

        $('[data-toggle="tooltip"]').tooltip({
            title: ko.i18n(params.tooltipMessageKey)
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});