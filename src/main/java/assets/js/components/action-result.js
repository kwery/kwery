define(["knockout", "jquery", "text!components/action-result.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.messages = params.messages;

        self.showSuccess = ko.computed(function(){
            return self.status() === "success";
        }, self);

        self.showFailure = ko.computed(function(){
            return self.status() === "failure";
        }, self);

        self.showInfo = ko.computed(function(){
            return self.status() === "info";
        }, self);

        $(document).ajaxError(function(event, jqxhr, settings, thrownError){
            if (window.console) {
                console.log("Exception:");
                console.log(jqxhr.responseText);
                console.log("HTTP status - " + jqxhr.status);
            }

            self.status("failure");
            self.messages([ko.i18n("server.error")]);
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
