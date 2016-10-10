define(["knockout", "jquery", "text!components/sql-query/add.html"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;
        self.query = ko.observable();
        self.cronExpression = ko.observable();
        self.label = ko.observable();
        self.datasource = ko.observable();

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.datasources = ko.observableArray([]);

        self.actionLabel = ko.observable(ko.i18n("create"));

        var isUpdate = params.sqlQueryId || false;

        var Datasource = function(id, label) {
            this.id = id;
            this.label = label;
        };

        $.ajax({
            url: "/api/datasource/all",
            type: "get",
            contentType: "application/json",
            success: function(datasources) {
                ko.utils.arrayForEach(datasources, function(datasource){
                    self.datasources.push(new Datasource(datasource.id, datasource.label));
                });

                if (isUpdate) {
                    self.actionLabel(ko.i18n("update"));
                    $.ajax({
                        url: "/api/sql-query/" + params.sqlQueryId,
                        type: "GET",
                        contentType: "application/json",
                        success: function(result) {
                            self.query(result.query);
                            self.cronExpression(result.cronExpression);
                            self.label(result.label);

                            ko.utils.arrayForEach(self.datasources(), function(datasource){
                                if (datasource.id == result.datasource.id) {
                                    self.datasource(datasource);
                                }
                            });
                        }
                    });
                }
            }
        });

        var validate = $('form').validate({
            debug: true,
            messages: {
                query: {
                    required: ko.i18n("query.validation"),
                    minlength: ko.i18n("query.validation")
                },
                cronExpression: {
                    required: ko.i18n("cron.expression.validation"),
                    minlength: ko.i18n("cron.expression.validation")
                },
                label: {
                    required: ko.i18n("label.validation"),
                    minlength: ko.i18n("label.validation")
                }
            }
        });

        self.submit = function(formElem) {
            if ($(formElem).valid()) {
                var sqlQuery = {
                    query: self.query(),
                    cronExpression: self.cronExpression(),
                    label: self.label(),
                    datasourceId: self.datasource().id
                };

                if (isUpdate) {
                    sqlQuery.id = params.sqlQueryId;
                }

                $.ajax("/api/sql-query/add", {
                    data: ko.toJSON(sqlQuery),
                    type: "post",
                    contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);

                        var messages = result.messages;
                        var fieldMessages = result.fieldMessages;

                        self.messages([]);
                        if (messages != null) {
                            ko.utils.arrayPushAll(self.messages, result.messages)
                        }

                        if (fieldMessages != null) {
                            ko.utils.arrayForEach(["query", "datasourceId", "cronExpression", "label"], function(elem){
                                if (elem in fieldMessages) {
                                    ko.utils.arrayPushAll(self.messages, fieldMessages[elem])
                                }
                            });
                        }
                    }
                });
            }
        };
        return self;
    }
    return { viewModel: viewModel, template: template };
});
