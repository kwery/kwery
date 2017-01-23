define(["knockout", "jquery", "text!components/report/add.html", "validator", "jquery-cron", "waitingmodal", "jstorage"],
    function (ko, $, template, validator, jqueryCron, waitingModal) {
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
        self.reportName = ko.observable("");
        self.cronExpression = ko.observable("");
        self.parentReportId = ko.observable(0);
        self.reportEmails = ko.observable("");

        self.queries = ko.observableArray([]);
        self.emptyReportNoEmailRule = ko.observable(false);

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

        var Report = function(id, name) {
            this.id = id;
            this.name = name;
        };

        self.reports = ko.observableArray([new Report("", ko.i18n("report.save.parent.report.id.select.default"))]);

        if (!isUpdate) {
            self.queries.push(new Query());
        }

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
                url: "/api/job/list",
                type: "POST",
                contentType: "application/json",
                data: ko.toJSON({
                    jobLabelId: 0
                }),
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
                        self.reportName(report.name);
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

                        if (jobModelHackDto.jobModel.labels.length > 0) {
                            //Pop the empty label value since we have labels to show
                            self.labelIds.pop();
                        }

                        $.each(jobModelHackDto.jobModel.labels, function(index, label){
                            self.labelIds.push(new LabelId(label.id));
                        });

                        self.emptyReportNoEmailRule(jobModelHackDto.jobModel.rules["EMPTY_REPORT_NO_EMAIL"] === undefined ? false : (jobModelHackDto.jobModel.rules["EMPTY_REPORT_NO_EMAIL"] === "true") );

                        self.refreshValidation();
                    }
                })
            }
        }).always(function(){
            waitingModal.hide();
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
                    name: self.reportName(),
                    title: self.title(),
                    //TODO - Updating to 0 turns into empty string
                    parentJobId: self.parentReportId() ? self.parentReportId() : 0,
                    emails: emails,
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
                    emptyReportNoEmailRule: self.emptyReportNoEmailRule()
                };

                if (isUpdate) {
                    report.id = reportId;
                }

                $.ajax({
                    url: "/api/job/save",
                    data: ko.toJSON(report),
                    type: "POST",
                    contentType: "application/json",
                    beforeSend: function() {
                        waitingModal.show();
                    },
                    success: function(result) {
                        if (result.status === "success") {
                            if ($.jStorage.storageAvailable()) {
                                $.jStorage.set("report:status", result.status, {TTL: (10 * 60 * 1000)});
                                $.jStorage.set("report:messages", [ko.i18n('report.save.success.message')], {TTL: (10 * 60 * 1000)});
                                window.location.href = "#report/list";
                            } else {
                                throw new Error("Not enough space available to store result in browser");
                            }
                        } else {
                            waitingModal.hide();
                            self.status(result.status);
                            self.messages(result.messages);
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
                    //Label has a parent label
                    var parentNodeInTree = findNode(root, parent.id);
                    if (parentNodeInTree !== undefined) {
                        //Parent label is already present in the tree, hence add the child label as a child of the existing parent
                        parentNodeInTree.addChild(new Node(child.label, child.id, parentNodeInTree));
                    } else {
                        //Parent label is not present in the tree, create a new parent node
                        var parentNode = new Node(parent.label, parent.id, root);
                        //Add the child label as a child of the parent node
                        parentNode.addChild(new Node(child.label, child.id, parentNode));
                        //Add parent node as a child of the root label
                        root.addChild(parentNode);
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

        //Get current labels
        $.ajax({
            url: "/api/job-label/list",
            type: "GET",
            contentType: "application/json",
            success: function (jobLabelModelHackDtos) {
                buildLabelTree(jobLabelModelHackDtos);
                populateDisplayLabels(root, 0);
            }
        });
        //Label related - end

        return self;
    }
    return { viewModel: viewModel, template: template };
});
