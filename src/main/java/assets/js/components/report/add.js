define(["knockout", "jquery", "text!components/report/add.html", "validator", "jquery-cron"], function (ko, $, template, validator, jqueryCron) {
    function viewModel(params) {
        var self = this;

        var reportId = params.reportId;
        var isUpdate = reportId !== undefined && reportId > 0;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        //Schedule options
        self.scheduleOption = ko.observable("cronExpression");
        $("#cronExpression").attr("data-validate", true);
        $("#parentReport").attr("data-validate", false);

        self.title = ko.observable("");
        self.reportLabel = ko.observable("");
        self.cronExpression = ko.observable("");
        self.parentReportId = ko.observable(0);
        self.reportEmails = ko.observable("");

        self.queries = ko.observableArray([]);

        self.scheduleOption.subscribe(function(newVal){
            $("#parentReport").attr("data-validate", false);
            $("#cronExpression").attr("data-validate", false);

            clearValidation($("#parentReport"));
            clearValidation($("#cronExpression"));

            if (newVal === "cronExpression") {
                $("#cronExpression").attr("data-validate", true);
            }

            if (newVal === "parentReport") {
                $("#parentReport").attr("data-validate", true);
            }

            self.refreshValidation();

            function clearValidation(obj) {
                obj.parent(".form-group").removeClass("has-error has-danger");
                obj.siblings(".with-errors").empty();
            }
        }, self);

        var Datasource = function(id, label) {
            this.id = id;
            this.label = label;
        };

        self.datasources = ko.observableArray([new Datasource("", ko.i18n("report.save.datasource.select.default"))]);

        var Query = function(query, queryTitle, queryLabel, datasourceId, id) {
            this.query = query;
            this.queryLabel = queryLabel;
            this.queryTitle = queryTitle;
            this.datasourceId = datasourceId;
            this.id = id;
        };

        var Report = function(id, label) {
            this.id = id;
            this.label = label;
        };

        self.reports = ko.observableArray([new Report("", ko.i18n("report.save.parent.report.id.select.default"))]);

        if (!isUpdate) {
            self.queries.push(new Query());
        }

        $.when(
            $.ajax({
                url: "/api/datasource/all",
                type: "get",
                contentType: "application/json",
                success: function(datasources) {
                    ko.utils.arrayForEach(datasources, function(datasource){
                        self.datasources.push(new Datasource(datasource.id, datasource.label));
                    });
                }
            }),
            $.ajax({
                url: "/api/job/list",
                type: "GET",
                contentType: "application/json",
                success: function(reports) {
                    ko.utils.arrayForEach(reports, function(jobModelHackDto){
                        var report = jobModelHackDto.jobModel;
                        if (isUpdate) {
                            if (report.id !== reportId) {
                                self.reports.push(new Report(report.id, report.label));
                            }
                        } else {
                            self.reports.push(new Report(report.id, report.label));
                        }
                    });
                }
            })
        ).done(function(){
            if (isUpdate) {
                $.ajax({
                    url: "/api/job/" + reportId,
                    type: "GET",
                    contentType: "application/json",
                    success: function(jobModelHackDto) {
                        var report = jobModelHackDto.jobModel;
                        self.title(report.title);
                        self.reportLabel(report.label);
                        self.reportEmails(report.emails.join(", "));

                        if (jobModelHackDto.parentJobModel != null) {
                            self.scheduleOption("parentReport");
                            self.parentReportId(jobModelHackDto.parentJobModel.id);
                        } else {
                            self.cronExpression(report.cronExpression);
                        }

                        $.each(report.sqlQueries, function(index, sqlQuery){
                            var query = new Query(sqlQuery.query, sqlQuery.title, sqlQuery.label, sqlQuery.datasource.id, sqlQuery.id);
                            self.queries.push(query);
                        });

                        self.refreshValidation();
                    }
                })
            }
        });


        self.addSqlQuery = function() {
            self.queries.push(new Query());
        };

        self.removeQuery = function(query) {
            self.queries.remove(query);
        };

        var cron = $("#cron").cron();

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
            if (e.isDefaultPrevented()) {
                // handle the invalid form...
            } else {
                var queries = [];

                ko.utils.arrayForEach(self.queries(), function(query){
                    queries.push({
                        query: query.query,
                        label: query.queryLabel,
                        title: query.queryTitle,
                        datasourceId: query.datasourceId,
                        id: query.id
                    });
                });

                var emails = $.grep($.map(self.reportEmails().split(","), $.trim), function(elem){
                    return elem !== null && elem !== "";
                });

                //Reset parent report id in case the option chosen was cron expression
                if (self.scheduleOption() !== "parentReport") {
                    self.parentReportId(0);
                }

                var report = {
                    cronExpression: self.scheduleOption() === "cronUi" ? cron.cron("value") : self.cronExpression(),
                    label: self.reportLabel(),
                    title: self.title(),
                    //TODO - Updating to 0 turns into empty string
                    parentJobId: self.parentReportId() ? self.parentReportId() : 0,
                    emails: emails,
                    sqlQueries: queries
                };

                if (isUpdate) {
                    report.id = reportId;
                }

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
