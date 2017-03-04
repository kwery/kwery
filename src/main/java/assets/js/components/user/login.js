define(["knockout", "jquery", "repo-dash", "router", "text!components/user/login.html", "ajaxutil", "waitingmodal", "jstorage"],
    function (ko, $, repoDash, router, template, ajaxUtil, waitingModal) {
    function viewModel(params) {
        var self = this;

        self.login = ko.observable(true);
        self.determineNextSteps = ko.observable(false);

        //To show save messages
        var status = $.jStorage.get("us:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("us:status");
        }

        var messages = $.jStorage.get("us:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("us:messages");
        }

        self.email = ko.observable();
        self.password = ko.observable();

        self.nextAction = ko.observable("");
        self.nextActionName = ko.observable("");

        self.submit = function() {
            ajaxUtil.waitingAjax({
                url: "/api/user/login",
                data: ko.toJSON({
                    email: self.email(),
                    password: self.password()
                }),
                type: "post", contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages);

                    if (result.status === "success") {
                        repoDash.user.setAuthenticated(true);
                        //Intended action was not login, but the user was not authenticated, hence login page was
                        //shown. Now, post login success, refresh the page
                        if (window.location.hash !== "#user/login") {
                            window.location.reload();
                        } else {
                            //Figure out what to do next
                            $.ajax({
                                before: function(){
                                    waitingModal.show();
                                },
                                url: "/api/onboarding/next-action",
                                type: "GET",
                                contentType: "application/json",
                                success: function(response){
                                    waitingModal.hide();
                                    switch (response.action) {
                                        case "ADD_DATASOURCE":
                                            window.location.href = "/#onboarding";
                                            break;
                                        case "ADD_JOB":
                                            window.location.href = "/#onboarding";
                                            break;
                                        case "SHOW_HOME_SCREEN":
                                            window.location.href = "/#report/list";
                                            break;
                                    }
                                }
                            });
                        }
                    } else {

                    }
                }
            });
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
