define(["knockout", "jquery", "text!components/sql-query/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.sqlQueries = ko.observableArray([]);
        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.executeNowButtonLabel = ko.observable("Execute Now");

        $.ajax({
            url: "/api/sql-query/list",
            type: "get",
            contentType: "application/json",
            success: function (result) {
                self.sqlQueries(
                    result.map(function(obj){
                        obj.queryLink = "/#sql-query/" + obj.id + "/execution-list/";
                        return obj;
                    })
                );
            }
        });

        self.delete = function(sqlQuery) {
            $.ajax({
                url: "/api/sql-query/delete/" + sqlQuery.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages(result.messages || []);

                    if (result.status === "success") {
                        self.sqlQueries.remove(sqlQuery);
                    }
                }
            });
        };

        self.executeNow = function(sqlQuery) {
            $.ajax({
                url: "/api/sql-query/one-off-execution/" + sqlQuery.id,
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    //TODO - Handle else case
                    if (result.status === "success") {
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
