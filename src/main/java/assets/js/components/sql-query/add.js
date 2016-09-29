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
                    minlength: ko.i18n("cron.expression.validation"),
                },
                label: {
                    required: ko.i18n("label.validation"),
                    minlength: ko.i18n("label.validation")
                }
            }
        });

        self.submit = function(formElem) {
            if ($(formElem).valid()) {
                $.ajax("/api/sql-query/add", {
                    data: ko.toJSON({
                        query: self.query(),
                        cronExpression: self.cronExpression(),
                        label: self.label(),
                        datasourceId: self.datasource().id
                    }),
                    type: "post", contentType: "application/json",
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
