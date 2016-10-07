define(["knockout", "jquery", "text!components/sql-query/execution-result.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.header = ko.observableArray([]);
        self.contents = ko.observableArray([]);
        self.fetchingResult = ko.observable(true);
        self.noResult = ko.observable(false);

        $.ajax({
            url: "/api/sql-query/" + params.sqlQueryId + "/execution/" + params.sqlQueryExecutionId,
            type: "get",
            contentType: "application/json",
            success: function (result) {
                if (result.length > 1) {
                    ko.utils.arrayPushAll(self.header, result[0]);
                    ko.utils.arrayPushAll(self.contents, result.slice(1, result.length));
                } else {
                    //TODO - Test case for no result
                    self.noResult(true);
                }
            }
        });

        self.fetchingResult = ko.observable(false);

        return self;
    }
    return { viewModel: viewModel, template: template };
});
