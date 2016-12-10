define(["knockout", "jquery", "text!components/report/add.html", "validator"], function (ko, $, template, validator) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.title = ko.observable();
        self.reportLabel = ko.observable();
        self.cronExpression = ko.observable();
        self.parentReportId = ko.observable();

        var Datasource = function(id, label) {
            this.id = id;
            this.label = label;
        };

        self.datasources = ko.observableArray([new Datasource("", ko.i18n("report.save.datasource.select.default"))]);

        var Query = function(query, queryLabel, datasourceId) {
            this.query = query;
            this.queryLabel = queryLabel;
            this.datasourceId = datasourceId;
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

        var Report = function(id, label) {
            this.id = id;
            this.label = label;
        };

        self.reports = ko.observableArray([new Report("", ko.i18n("report.save.parent.report.id.select.default"))]);

        $.ajax({
            url: "/api/job/list",
            type: "GET",
            contentType: "application/json",
            success: function(reports) {
                ko.utils.arrayForEach(reports, function(report){
                    self.reports.push(new Report(report.id, report.label));
                });
            }
        });

        self.cronExpressionEnabled = ko.observable(true);
        self.parentReportEnabled = ko.observable(false);

        self.cronExpressionEnableText = ko.computed(function(){
            if (self.cronExpressionEnabled()) {
                return ko.i18n("report.save.disable");
            } else {
                return ko.i18n("report.save.enable");
            }
        }, self);

        self.parentReportEnableText = ko.computed(function(){
            if (self.parentReportEnabled()) {
                return ko.i18n("report.save.disable");
            } else {
                return ko.i18n("report.save.enable");
            }
        }, self);

        self.enableParentReport = function(){
            self.cronExpressionEnabled(!self.cronExpressionEnabled());
            self.parentReportEnabled(!self.parentReportEnabled());

            $("#parentReport").attr("data-validate", self.parentReportEnabled());
            $("#cronExpression").attr("data-validate", self.cronExpressionEnabled());
            self.refreshValidation();

            return false;
        };

        self.enableCronExpression = function() {
            self.parentReportEnabled(!self.parentReportEnabled());
            self.cronExpressionEnabled(!self.cronExpressionEnabled());

            $("#parentReport").attr("data-validate", self.parentReportEnabled());
            $("#cronExpression").attr("data-validate", self.cronExpressionEnabled());
            self.refreshValidation();

            if (!self.cronExpressionEnabled()) {
                $("#cronExpression").attr("data-validate", false);
            } else {
                $("#cronExpression").attr("data-validate", true);
            }

            //$("#reportForm").trigger('reset');
            self.refreshValidation();
            return false;
        };

        self.queries = ko.observableArray([new Query()]);

        self.addSqlQuery = function() {
            self.queries.push(new Query());
        };

        self.removeQuery = function(query) {
            self.queries.remove(query);
        };

        $("#reportForm").validator({
            disable: false,
            custom: {
                "labelvalidation": function ($el) {
                    var sameValue = 0;

                    $('.sql-query-label').each(function(){
                        if ($(this).val() !== '' && $el.val() === $(this).val()) {
                            sameValue = sameValue + 1;
                        }
                    });

                    if (sameValue > 1) {
                        return ko.i18n('report.save.duplicate.sql.query.label.error');
                    }
                }
            }
        }).on("submit", function (e) {
            debugger;
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                var queries = [];

                ko.utils.arrayForEach(self.queries(), function(query){
                    queries.push({
                        query: query.query,
                        label: query.queryLabel,
                        datasourceId: query.datasourceId
                    });
                });

                var report = {
                    cronExpression: self.cronExpression(),
                    label: self.reportLabel(),
                    title: self.title(),
                    parentJobId: self.parentReportId(),
                    sqlQueries: queries
                };

                $.ajax({
                    url: "/api/job/save",
                    data: ko.toJSON(report),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);
                        if (result.status === 'failure') {
                            self.messages(result.messages);
                        } else {
                            self.messages([ko.i18n('report.save.success.message')]);
                        }
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
