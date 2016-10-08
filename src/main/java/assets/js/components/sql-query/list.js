define(["knockout", "jquery", "text!components/sql-query/list.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.sqlQueries = ko.observableArray([]);

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

        return self;
    }
    return { viewModel: viewModel, template: template };
});
