define(["knockout", "jquery", "text!components/report/add.html", "validator"], function (ko, $, template, validator) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.datasources = ko.observableArray([]);

        self.title = ko.observable();
        self.reportLabel = ko.observable();
        self.cronExpression = ko.observable();

        var Datasource = function(id, label) {
            this.id = id;
            this.label = label;
        };

        var Query = function(query, queryLabel, datasource) {
            this.query = query;
            this.queryLabel = queryLabel;
            this.datasource = datasource;
        };

        $.ajax({
            url: "/api/datasource/all",
            type: "get",
            contentType: "application/json",
            success: function(datasources) {
                ko.utils.arrayForEach(datasources, function(datasource){
                    self.datasources.push(new Datasource(datasource.id, datasource.label));
                });
            }
        });

        self.queries = ko.observableArray([new Query()]);

        self.addSqlQuery = function() {
            self.queries.push(new Query());
        };

        self.removeQuery = function(query) {
            self.queries.remove(query);
        };

        $("#reportForm").validator({disable: false}).on("submit", function (e) {
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                var queries = [];

                ko.utils.arrayForEach(self.queries(), function(query){
                    queries.push({
                        query: query.query,
                        label: query.queryLabel,
                        datasourceId: query.datasource.id
                    });
                });

                var report = {
                    cronExpression: self.cronExpression(),
                    label: self.reportLabel(),
                    title: self.title(),
                    sqlQueries: queries
                };

                $.ajax({
                    url: "/api/job/save",
                    data: ko.toJSON(report),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);
                        self.messages([ko.i18n('report.save.success.message')]);
                    }
                });
            }

            return false;
        });

        self.refreshValidation = function() {
            $("#reportForm").validator("update");
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});