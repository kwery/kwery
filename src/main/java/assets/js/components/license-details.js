define(["knockout", "jquery", "repo-dash", "text!components/license-details.html"], function (ko, $, repoDash, template) {
    function viewModel(params) {
        var self = this;

        self.showTrialExpiryMessage = ko.observable(false);
        self.showTrialMessage = ko.observable(false);

        self.disappear = function(){
            self.showTrialMessage(false);
        };

        self.checkLicense = function() {
            $.getJSON("/api/license", function(licenseDetails){
                //Reset
                self.showTrialMessage(false);
                self.showTrialExpiryMessage(false);
                if (!licenseDetails.license) {
                    if (licenseDetails.trialPeriod) {
                        self.showTrialMessage(true);
                        self.showTrialExpiryMessage(false);
                    } else {
                        self.showTrialMessage(false);
                        self.showTrialExpiryMessage(true);
                    }
                }
            });
        };

        setInterval(function(){
            self.checkLicense();
        }, (60 * 60 * 1000));

        self.checkLicense();

        return self;
    }
    return { viewModel: viewModel, template: template };
});
