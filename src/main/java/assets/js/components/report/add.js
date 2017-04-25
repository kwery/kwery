define(["knockout", "jquery", "text!components/report/add.html", "validator", "jquery-cron", "waitingmodal", "ajaxutil", "jstorage"],
    function (ko, $, template, validator, jqueryCron, waitingModal, ajaxUtil) {
    function ViewModel(params) {
        var self = this;

        var reportId = params.reportId;

        //For copy report flow
        if (reportId === undefined && typeof params.toCopyFromReportId === "function") {
            reportId = params.toCopyFromReportId();
        }

        var isUpdate = reportId !== undefined && reportId > 0;
        var isCopy = params.isCopy !== undefined && params.isCopy() === true;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        //Is this onboarding flow?
        if (params["?q"] !== undefined) {
            if (params["?q"].onboarding && params["?q"].fromDatasource) {
                self.status("info");
                self.messages([ko.i18n("onboarding.report.add.post.datasource")]);
            } else if (params["?q"].onboarding) {
                self.status("info");
                self.messages([ko.i18n("onboarding.report.add")]);
            }
        }

        //Schedule options
        self.scheduleOption = ko.observable("cronExpression");
        $("#cronExpression").attr("data-validate", true);
        $("#parentReport").attr("data-validate", false);

        self.title = ko.observable("");
        self.reportName = ko.observable("");
        self.cronExpression = ko.observable("");
        self.parentReportId = ko.observable(0);
        self.reportEmails = ko.observable("");
        self.failureAlertEmails = ko.observable("");

        self.queries = ko.observableArray([]);
        self.emptyReportNoEmailRule = ko.observable(false);

        self.smtpConfiguration = ko.observable(false);
        self.senderDetailsConfiguration = ko.observable(false);

        self.enableEmails = ko.computed(function(){
            return self.smtpConfiguration() && self.senderDetailsConfiguration();
        });

        self.urlConfigured = ko.observable(false);

        self.jobRuleModelId = ko.observable();
        self.sequentialSqlQueryExecution = ko.observable(false);
        self.stopExecutionOnSqlQueryFailure = ko.observable(true);

        self.showStopExecutionOnSqlQueryFailure = ko.computed(function(){
            return self.sequentialSqlQueryExecution();
        }, this);

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

        var Query = function(query, queryTitle, queryLabel, datasourceId, id, emailSettingId, includeInBody, includeAsAttachment) {
            this.query = query;
            this.queryLabel = queryLabel;
            this.queryTitle = queryTitle;
            this.datasourceId = datasourceId;
            this.id = id;
            this.emailSettingId = emailSettingId;
            this.includeInBody = includeInBody;
            this.includeAsAttachment = includeAsAttachment;
        };

        var Report = function(id, name) {
            this.id = id;
            this.name = name;
        };

        self.reports = ko.observableArray([new Report("", ko.i18n("report.save.parent.report.id.select.default"))]);

        if (!isUpdate) {
            var query = new Query();
            query.includeAsAttachment = true;
            query.includeInBody = true;
            self.queries.push(query);
        }

        self.fetchReport = function(reportId, executionId){
            $.ajax({
                url: "/api/job/" + reportId + "/execution",
                data: ko.toJSON({
                    pageNumber: 0,
                    resultCount: 1,
                    executionId: executionId
                }),
                type: "POST",
                contentType: "application/json",
                success: function(response) {
                    if (response.jobExecutionDtos.length > 0 && response.jobExecutionDtos[0].status !== 'ONGOING') {
                        waitingModal.hide();
                        document.location.href = "/#report/" + reportId + "/execution/" + executionId;
                    } else {
                        setTimeout(function(){
                            self.fetchReport(reportId, executionId)
                        }, 5000);
                    }
                }
            });
        };

        self.executeReport = function(reportId) {
            waitingModal.show(ko.i18n('report.save.generate.message'));
            $.ajax({
                url: "/api/job/" + reportId + "/execute",
                type: "POST",
                contentType: "application/json",
                success: function(executeResponse) {
                    self.fetchReport(reportId, executeResponse.executionId);
                }
            });
            return false;
        };

        waitingModal.show();
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
                url: "/api/job/list-all",
                type: "GET",
                contentType: "application/json",
                success: function(reports) {
                    ko.utils.arrayForEach(reports, function(jobModelHackDto){
                        var report = jobModelHackDto.jobModel;
                        if (isUpdate) {
                            if (report.id !== reportId) {
                                self.reports.push(new Report(report.id, report.name));
                            }
                        } else {
                            self.reports.push(new Report(report.id, report.name));
                        }
                    });
                }
            }),
            $.ajax({
                url: "/api/job-label/list",
                type: "GET",
                contentType: "application/json",
                success: function (jobLabelModelHackDtos) {
                    buildLabelTree(jobLabelModelHackDtos);
                    populateDisplayLabels(root, 0);
                }
            }),
            $.ajax("/api/mail/smtp-configuration", {
                type: "GET",
                contentType: "application/json",
                success: function(conf) {
                    if (conf != null) {
                        self.smtpConfiguration(true);
                    }
                }
            }),
            $.ajax("/api/mail/email-configuration", {
                type: "GET",
                contentType: "application/json",
                success: function(conf) {
                    if (conf != null) {
                        self.senderDetailsConfiguration(true);
                    }
                }
            }),
            $.ajax({
                url: "/api/url-configuration",
                type: "GET",
                contentType: "application/json",
                success: function(urlSetting) {
                    if (urlSetting !== null) {
                        self.urlConfigured(true);
                    }
                }
            }),
            (function(){
                if (isUpdate) {
                    return $.ajax({
                        url: "/api/job/" + reportId,
                        type: "GET",
                        contentType: "application/json",
                        success: function(jobModelHackDto) {
                            var report = jobModelHackDto.jobModel;
                            self.title(report.title);
                            self.reportName(report.name);
                            self.reportEmails(report.emails.join(", "));
                            self.failureAlertEmails(report.failureAlertEmails.join(", "));

                            if (jobModelHackDto.parentJobModel != null) {
                                self.scheduleOption("parentReport");
                                self.parentReportId(jobModelHackDto.parentJobModel.id);
                            } else {
                                self.cronExpression(report.cronExpression);
                            }

                            if (report.jobRuleModel != null) {
                                if (!isCopy) {
                                    self.jobRuleModelId(report.jobRuleModel.id);
                                }
                                self.sequentialSqlQueryExecution(report.jobRuleModel.sequentialSqlQueryExecution);
                                self.stopExecutionOnSqlQueryFailure(report.jobRuleModel.stopExecutionOnSqlQueryFailure);
                            }

                            $.each(report.sqlQueries, function(index, sqlQuery){
                                var emailSettingId = null;
                                var includeInBody = true;
                                var includeAsAttachment = true;

                                if (sqlQuery.sqlQueryEmailSettingModel != null) {
                                    if (!isCopy) {
                                        emailSettingId = sqlQuery.sqlQueryEmailSettingModel.id;
                                    }
                                    includeInBody = sqlQuery.sqlQueryEmailSettingModel.includeInEmailBody;
                                    includeAsAttachment = sqlQuery.sqlQueryEmailSettingModel.includeInEmailAttachment;
                                }

                                var sqlQueryId = null;
                                if (!isCopy) {
                                    sqlQueryId = sqlQuery.id;
                                }

                                var query = new Query(sqlQuery.query, sqlQuery.title, sqlQuery.label, sqlQuery.datasource.id, sqlQueryId,
                                    emailSettingId, includeInBody, includeAsAttachment);

                                self.queries.push(query);
                            });

                            if (jobModelHackDto.jobModel.labels.length > 0) {
                                //Pop the empty label value since we have labels to show
                                self.labelIds.pop();
                            }

                            $.each(jobModelHackDto.jobModel.labels, function(index, label){
                                self.labelIds.push(new LabelId(label.id));
                            });

                            self.emptyReportNoEmailRule(jobModelHackDto.jobModel.rules["EMPTY_REPORT_NO_EMAIL"] === undefined ? false : (jobModelHackDto.jobModel.rules["EMPTY_REPORT_NO_EMAIL"] === "true") );

                        }
                    })
                } else {
                    return $.when();
                }
            })()
        ).always(function(){
            waitingModal.hide();
            self.refreshValidation();

            //Is this a save and generate report request?
            if (isUpdate && $.jStorage.get("report:execute") === true) {
                $.jStorage.deleteKey("report:execute");
                self.executeReport(reportId);
            }
        });

        self.addSqlQuery = function() {
            var query = new Query();
            //By default, we want the below checked
            query.includeInBody = true;
            query.includeAsAttachment = true;

            self.queries.push(query);
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
                        id: query.id,
                        sqlQueryEmailSetting: {
                            id: query.emailSettingId,
                            includeInEmailBody: query.includeInBody,
                            includeInEmailAttachment: query.includeAsAttachment
                        }
                    });
                });

                var emails = $.grep($.map(self.reportEmails().split(","), $.trim), function(elem){
                    return elem !== null && elem !== "";
                });

                var failureAlertEmails = $.grep($.map(self.failureAlertEmails().split(","), $.trim), function(elem){
                    return elem !== null && elem !== "";
                });

                //Reset parent report id in case the option chosen was cron expression
                if (self.scheduleOption() !== "parentReport") {
                    self.parentReportId(0);
                }

                var report = {
                    cronExpression: self.scheduleOption() === "cronUi" ? cron.cron("value") : self.cronExpression(),
                    name: self.reportName(),
                    title: self.title(),
                    //TODO - Updating to 0 turns into empty string
                    parentJobId: self.parentReportId() ? self.parentReportId() : 0,
                    emails: emails,
                    failureAlertEmails: failureAlertEmails,
                    sqlQueries: queries,
                    labelIds: (function(){
                        var ids = [];
                        ko.utils.arrayForEach(self.labelIds(), function(labelId){
                            if (labelId.id !== "") {
                                ids.push(labelId.id);
                            }
                        });
                        return ids;
                    })(),
                    emptyReportNoEmailRule: self.emptyReportNoEmailRule(),
                    jobRuleModel: {
                        id: self.jobRuleModelId(),
                        sequentialSqlQueryExecution: self.sequentialSqlQueryExecution(),
                        stopExecutionOnSqlQueryFailure: self.sequentialSqlQueryExecution() === true ? self.stopExecutionOnSqlQueryFailure() : false //This can have a value only when sequential
                        //execution is enabled
                    }
                };

                if (isUpdate && !isCopy) {
                    report.id = reportId;
                }

                $.ajax({
                    url: "/api/job/save",
                    data: ko.toJSON(report),
                    type: "POST",
                    contentType: "application/json",
                    beforeSend: function(){
                        waitingModal.show();
                    },
                    success: function(result) {
                        waitingModal.hide();
                        if (result.status === "failure") {
                            self.status(result.status);
                            self.messages(result.messages);
                        } else {
                            var val = $("button[type=submit][clicked=true]").val();
                            if (val === "save") {
                                window.location.href = "/#report/list";
                                if ($.jStorage.storageAvailable()) {
                                    $.jStorage.set("report:status", "success", {TTL: (10 * 60 * 1000)});
                                    $.jStorage.set("report:messages", [ko.i18n('report.save.success.message')], {TTL: (10 * 60 * 1000)});
                                } else {
                                    throw new Error("Not enough space available to store result in browser");
                                }
                            } else {
                                if (isUpdate) {
                                    self.executeReport(result.reportId);
                                } else {
                                    //We do this so that pressing back button on the report page takes the user back to the
                                    //report edit page.
                                    $.jStorage.set("report:execute", true, {TTL: (10 * 60 * 1000)});
                                    window.location.href = "/#report/" + result.reportId;
                                }
                            }
                        }
                    }
                });
            }

            return false;
        });

        self.refreshValidation = function() {
            $("#reportForm").validator("update");
        };

        var LabelId = function(id) {
            this.id = id;
        };

        self.labelIds = ko.observableArray([new LabelId(0)]);

        self.addLabel = function() {
            self.labelIds.push(new LabelId(0));
        };

        self.removeLabel = function(labelId) {
            self.labelIds.remove(labelId);
        };

        //To figure out which button was clicked on form submit
        $("form button[type=submit]").on("click", function() {
            $("button[type=submit]", $(this).parents("form")).removeAttr("clicked");
            $(this).attr("clicked", "true");
        });

        //TODO - Duplicated code with add label page, needs to be refactored into a common code
        //Label related - start
        var Node = function(label, id, parent) {
            this.label = label;
            this.id = id;
            this.children = [];
            this.parent = parent;

            this.addChild = function(node) {
                this.children.push(node);
            };

            this.remove = function() {
                if (this.parent == null) {
                    throw Error("Cannot delete root");
                } else {
                    for (var i = 0; i < this.parent.children.length; ++i) {
                        if (this.parent.children[i] == this) {
                            this.parent.children.splice(i, 1);
                            break;
                        }
                    }
                }
            };
        };

        var moveNode = function(id, newParentId) {
            var node = findNode(root, id);
            node.remove();
            var newParentNode = findNode(root, newParentId);
            newParentNode.addChild(node);
            node.parent = newParentNode;
        };

        //This is n2, but we are not bothered about efficiency at this scale
        var findNode = function(node, id) {
            if (node.id === id) {
                return node;
            } else {
                for (var i = 0; i < node.children.length; ++i) {
                    var ret = findNode(node.children[i], id);
                    if (ret !== undefined) {
                        return ret;
                    }
                }
            }
        };

        //A dummy root node to serve as the parent of the label tree
        var root = new Node("", 0, null);

        var DisplayLabel = function(id, formattedLabel, label) {
            this.id = id;
            this.formattedLabel = formattedLabel;
            this.label = label;
        };

        self.displayLabels = ko.observableArray([new DisplayLabel("", "", "")]);

        //Add spaces and construct a tree like structure to show in select drop down
        var populateDisplayLabels = function(node, count) {
            if (node.id !== 0) {
                var label = Array(count).join("&nbsp;&nbsp;&nbsp;&nbsp;") + node.label;
                self.displayLabels.push(new DisplayLabel(node.id, label, node.label));
            }
            $.each(node.children, function(index, node) {
                return populateDisplayLabels(node, count + 1);
            });
        };

        var buildLabelTree = function(jobLabelModelHackDtos) {
            //Create tree using labels
            ko.utils.arrayForEach(jobLabelModelHackDtos, function (jobLabelModelHackDto) {
                var parent = jobLabelModelHackDto.parentJobLabelModel;
                var child = jobLabelModelHackDto.jobLabelModel;

                if (parent != undefined) {
                    var parentNode = findNode(root, parent.id);
                    var childNode = findNode(root, child.id);

                    if (parentNode !== undefined) {
                        //Parent is already present in the tree
                        if (childNode !== undefined) {
                            //Child is already present in the tree
                            //Move the child from old parent to new parent
                            moveNode(child.id, parent.id);
                        } else {
                            //Child is not present in the tree
                            //Add the child label as a child of the existing parent
                            parentNode.addChild(new Node(child.label, child.id, parentNode));
                        }
                    } else {
                        //Parent label is not present in the tree
                        if (childNode !== undefined) {
                            //Child is already present in the tree
                            //Add parent
                            var parentNode = new Node(parent.label, parent.id, root);
                            root.addChild(parentNode);
                            //Move child to new parent
                            moveNode(child.id, parentNode.id);
                        } else {
                            //Parent label is not present in the tree, create a new parent node
                            var parentNode = new Node(parent.label, parent.id, root);
                            //Add the child label as a child of the parent node
                            parentNode.addChild(new Node(child.label, child.id, parentNode));
                            //Add parent node as a child of the root label
                            root.addChild(parentNode);
                        }
                    }
                } else {
                    //Label does not have a parent
                    var nodeInTree = findNode(root, child.id);
                    if (nodeInTree === undefined) {
                        //Node is not present in the tree, hence create a new label node and add it as child of the root node
                        root.addChild(new Node(child.label, child.id, root));
                    } else {
                        //Label is already present in the tree. Added as the parent of some other label, no action to take
                    }
                }
            });
        };
        //Label related - end

        return self;
    }

    return { viewModel: ViewModel, template: template };
});
