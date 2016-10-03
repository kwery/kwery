define(["knockout", "jquery", "text!components/kill-sql-query.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.killActionLabel = ko.observable(ko.i18n("kill"));
        self.isExecuting = ko.observable(true);
        self.showKillButton = ko.observable(true);

        self.killSqlQuery = function() {
            self.killActionLabel(ko.i18n("killing"));

            $.ajax("/api/sql-query/kill/" + params.sqlQueryId, {
                data: ko.toJSON({
                    sqlQueryExecutionId: params.sqlQueryExecutionId
                }),
                type: "post", contentType: "application/json",
                success: function(result) {
                    if (result.status === 'success') {
                        self.killActionLabel(ko.i18n("killed"));
                        self.isExecuting(false);
                    } else {
                        self.showKillButton(false);
                    }
                }
            });
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
