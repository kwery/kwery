define(["knockout", "jquery", "text!components/datasource/add.html", "ajaxutil", "validator"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;
        self.username = ko.observable();
        self.password = ko.observable("");
        self.url = ko.observable();
        self.database = ko.observable();
        self.port = ko.observable();
        self.label = ko.observable();
        self.datasourceType = ko.observable("");

        self.showDatabase = ko.observable(false);

        self.datasourceType.subscribe(function(val){
            if (val === 'POSTGRESQL') {
                self.showDatabase(true);
                $("#database").attr("data-validate", true);
            } else {
                self.showDatabase(false);
                $("#database").attr("data-validate", false);
            }
            self.refreshValidation();
        });

        var DatasourceType = function(label, value) {
            this.label = label;
            this.value = value;
        };

        //TODO - Should be picked from the backend instead of being hardcoded here
        self.datasourceTypes = ko.observableArray([
            new DatasourceType(ko.i18n("datasource.add.type.select"), ""),
            new DatasourceType("MYSQL", "MYSQL"),
            new DatasourceType("POSTGRESQL", "POSTGRESQL")
        ]);

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        var isUpdate = params.datasourceId !== undefined;

        if (isUpdate) {
            self.actionLabel = ko.observable(ko.i18n('update'));
            self.title = ko.observable(ko.i18n('datasource.update.title'))
        } else {
            self.actionLabel = ko.observable(ko.i18n('create'));
            self.title = ko.observable(ko.i18n('datasource.add.title'))
        }

        $("#addDatasourceForm").validator({
            disable: false
        }).on("submit", function(e){
            if (!e.isDefaultPrevented()) {
                var datasource = {
                    url: self.url(),
                    port: self.port(),
                    username: self.username(),
                    password: self.password(),
                    label: self.label(),
                    type: self.datasourceType(),
                    database: self.database()
                };

                if (isUpdate) {
                    datasource.id = params.datasourceId;
                }

                ajaxUtil.waitingAjax( {
                    url: "/api/datasource/add-datasource",
                    data: ko.toJSON(datasource),
                    type: "POST",
                    contentType: "application/json",
                    success: function(result) {
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                });
            }

            return false;
        });

        self.refreshValidation = function() {
            $("#addDatasourceForm").validator("update");
        };

        if (isUpdate) {
            ajaxUtil.waitingAjax({
                url: "/api/datasource/" + params.datasourceId,
                type: "GET",
                contentType: "application/json",
                success: function(datasource) {
                    self.username(datasource.username);
                    self.password(datasource.password);
                    self.url(datasource.url);
                    self.port(datasource.port);
                    self.label(datasource.label);
                    self.datasourceType(datasource.type);
                    self.database(datasource.database);
                }
            })
        }

        return self;
    }
    return { viewModel: viewModel, template: template };
});
