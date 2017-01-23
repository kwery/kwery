define(["knockout", "jquery", "text!components/report/list.html", "ajaxutil", 'waitingmodal', "jstorage"], function (ko, $, template, ajaxUtil, waitingModal) {
    function viewModel(params) {
        var self = this;

        //To show save messages
        var status = $.jStorage.get("report:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("report:status");
        }

        var messages = $.jStorage.get("report:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("report:messages");
        }

        self.reports = ko.observableArray([]);

        self.executeReport = function(report) {
            ajaxUtil.waitingAjax({
                url: "/api/job/" + report.id + "/execute",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    self.status(result.status);
                    self.messages([ko.i18n("report.list.execute.now.success")]);
                }
            })
        };

        self.deleteReport = function(report) {
            ajaxUtil.waitingAjax({
                url: "/api/job/" + report.id + "/delete",
                type: "POST",
                contentType: "application/json",
                success: function(result) {
                    if (result.status === "success") {
                        self.status(result.status);
                        self.messages([ko.i18n("report.list.delete.success")]);
                        self.reports.remove(report);
                    } else {
                        self.status(result.status);
                        self.messages(result.messages);
                    }
                }
            })
        };

        self.reportLabelId = ko.observable(0);

        //Label - start
        //TODO - Duplicated code with add label page, needs to be refactored into a common code
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
        //Label - end

        self.filter = function(showWaitingModal) {
            $.ajax({
                url: "/api/job/list",
                type: "POST",
                contentType: "application/json",
                data: ko.toJSON({
                    jobLabelId: self.reportLabelId()
                }),
                beforeSend: function() {
                    if (showWaitingModal) {
                        waitingModal.show();
                    }
                },
                success: function (result) {
                    var reports = [];
                    ko.utils.arrayForEach(result, function(jobModelHackDto){
                        jobModelHackDto.jobModel.executionLink = "/#report/" + jobModelHackDto.jobModel.id + "/execution-list";
                        jobModelHackDto.jobModel.reportLink = "/#report/" + jobModelHackDto.jobModel.id;
                        reports.push(jobModelHackDto.jobModel);
                    });

                    self.reports(reports);
                }
            }).always(function(){
                if (showWaitingModal) {
                    waitingModal.hide();
                }
            });
        };

        waitingModal.show();
        $.when(
            self.filter(false),
            $.ajax({
                url: "/api/job-label/list",
                type: "GET",
                contentType: "application/json",
                success: function (jobLabelModelHackDtos) {
                    buildLabelTree(jobLabelModelHackDtos);
                    populateDisplayLabels(root, 0);
                }
            })
        ).always(function(){
            waitingModal.hide();
        });

        return self;
    }
    return { viewModel: viewModel, template: template };
});
