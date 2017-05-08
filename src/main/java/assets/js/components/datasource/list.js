define(["knockout", "jquery", "text!components/datasource/list.html", "ajaxutil", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        //To show save messages
        var status = $.jStorage.get("ds:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("ds:status");
        }

        var messages = $.jStorage.get("ds:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("ds:messages");
        }

        self.datasources = ko.observableArray([]);

        ajaxUtil.waitingAjax({
            url: "/api/datasource/all",
            type: "get",
            contentType: "application/json",
            success: function (result) {
                ko.utils.arrayForEach(result, function(datasource){
                    datasource.updateLink = "/#datasource/" + datasource.id
                });
                self.datasources(result);
            }
        }, "listAllDatasources");

        self.delete = function(datasource) {
            ajaxUtil.waitingAjax({
                url: "/api/datasource/delete/" + datasource.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages || []);

                    if (result.status === "success") {
                        self.datasources.remove(datasource);
                    }
                }
            }, "deleteDatasource");
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
