define(["knockout", "jquery", "text!components/action-result.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.status = params.status;
        self.messages = params.messages;

        self.status.extend({notify: "always"});
        self.status.subscribe(function(){
            $('body').scrollTop(0);
        }, self);

        self.showSuccess = ko.computed(function(){
            var ret = self.status() === "success";
            if (ret) {
                $('body').scrollTop(0);
            }
            return ret;
        }, self);

        self.showFailure = ko.computed(function(){
            var ret = self.status() === "failure";
            if (ret) {
                $('body').scrollTop(0);
            }
            return ret;
        }, self);

        self.showInfo = ko.computed(function(){
            var ret = self.status() === "info";
            if (ret) {
                $('body').scrollTop(0);
            }
            return ret;
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
