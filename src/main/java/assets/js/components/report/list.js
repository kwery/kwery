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

        //Only used for testing
        self.fluentField = ko.observable(-1);
        var row = 0;
        self.incrementRowCount = function(){
            row = row + 1;
            self.fluentField(row);
        };

        //Pagination
        //No of results to show in the page
        var RESULT_COUNT = 10;
        var pageNumber = 0;
        var resultCount = RESULT_COUNT;
        var reportLabelId = 0;
        var search = "";

        var reportLabelIdSubscription = null;

        if (params["?q"] !== undefined) {
            resultCount = params["?q"].resultCount || RESULT_COUNT;
            pageNumber = params["?q"].pageNumber || pageNumber;
            reportLabelId = params["?q"].reportLabelId || reportLabelId;

            try {
                search = decodeURIComponent(params["?q"].search || search);
            } catch(e) {
                self.status("failure");
                self.messages([ko.i18n('report.list.search.invalid')]);
            }
        }

        self.pageNumber = ko.observable(pageNumber);
        self.pageNumber.extend({notify: 'always'});
        self.resultCount = ko.observable(resultCount);
        self.totalCount = ko.observable(0);
        self.search = ko.observable(search);

        //If the value is set here, post the ajax call which populates the values, the selected option is not retained
        //Hence, this value is set in the ajax callback
        self.reportLabelId = ko.observable();

        self.reports = ko.observableArray([]);

        self.executeReport = function(report) {
            waitingModal.show(ko.i18n('report.list.execute.now.waiting'));
            $.ajax({
                url: "/api/job/" + report.id + "/execute",
                type: "POST",
                contentType: "application/json",
                success: function(executeResponse) {
                    fetchReport(report.id, executeResponse.executionId);
                }
            });
            return false;
        };

        var fetchReport = function(reportId, executionId){
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
                           fetchReport(reportId, executionId)
                       }, 5000);
                    }
                }
            });
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

        //Get current labels
        //Label - end

        //Pagination - start
        self.nextStatus = ko.pureComputed(function () {
            if (self.totalCount() <= ((self.pageNumber() + 1) * self.resultCount())) {
                return "disabled";
            }
            return "";
        });

        self.previousStatus = ko.pureComputed(function(){
            if (self.pageNumber() <= 0) {
                return "disabled";
            }
            return "";
        }, self);

        self.previous = function() {
            self.pageNumber(self.pageNumber() - 1);
        };

        self.next = function() {
            self.pageNumber(self.pageNumber() + 1);
        };

        self.navigate = function () {
            window.location.href = "/#report/list/?" +
                "pageNumber=" + self.pageNumber() +
                "&resultCount=" + self.resultCount() +
                "&search=" + encodeURIComponent(self.search()) +
                "&reportLabelId=" + self.reportLabelId();
        };

        self.pageNumber.subscribe(function(){
            self.navigate();
        });
        //Pagination - end

        waitingModal.show();
        $.when(
            (function() {
                if (self.search() !== '') {
                    return $.ajax({
                        url: "/api/job/search",
                        type: "POST",
                        contentType: "application/json",
                        data: ko.toJSON({
                            firstResult: (self.pageNumber() * self.resultCount()),
                            maxResults: self.resultCount() + 1,
                            phrase: self.search()
                        }),
                        success: function (jobDtos) {
                            self.totalCount((self.pageNumber() * self.resultCount()) + jobDtos.length);

                            //We get one more result to assist in pagination
                            if (jobDtos.length > self.resultCount()) {
                                jobDtos = jobDtos.slice(0, self.resultCount())
                            }

                            var reports = [];
                            ko.utils.arrayForEach(jobDtos, function(jobModelHackDto){
                                jobModelHackDto.jobModel.executionLink = "/#report/" + jobModelHackDto.jobModel.id + "/execution-list";
                                jobModelHackDto.jobModel.copyLink = "/#report/" + jobModelHackDto.jobModel.id + "/copy";
                                jobModelHackDto.jobModel.reportLink = "/#report/" + jobModelHackDto.jobModel.id;
                                jobModelHackDto.jobModel.lastExecution = jobModelHackDto.lastExecution;
                                jobModelHackDto.jobModel.nextExecution = jobModelHackDto.nextExecution;

                                reports.push(jobModelHackDto.jobModel);
                            });

                            self.reports(reports);
                        }
                    })
                } else {
                    return $.ajax({
                        url: "/api/job/list",
                        type: "POST",
                        contentType: "application/json",
                        data: ko.toJSON({
                            pageNumber: self.pageNumber(),
                            resultCount: self.resultCount(),
                            //Observable is intentionally not used here as it is set later
                            jobLabelId: reportLabelId
                        }),
                        success: function (jobListDto) {
                            var reports = [];
                            ko.utils.arrayForEach(jobListDto.jobModelHackDtos, function(jobModelHackDto){
                                jobModelHackDto.jobModel.executionLink = "/#report/" + jobModelHackDto.jobModel.id + "/execution-list";
                                jobModelHackDto.jobModel.reportLink = "/#report/" + jobModelHackDto.jobModel.id;
                                jobModelHackDto.jobModel.copyLink = "/#report/" + jobModelHackDto.jobModel.id + "/copy";
                                jobModelHackDto.jobModel.lastExecution = jobModelHackDto.lastExecution;
                                jobModelHackDto.jobModel.nextExecution = jobModelHackDto.nextExecution;

                                reports.push(jobModelHackDto.jobModel);
                            });

                            self.reports(reports);
                            self.totalCount(jobListDto.totalCount);
                        }
                    })
                }
            })(),
            $.ajax({
                url: "/api/job-label/list",
                type: "GET",
                contentType: "application/json",
                success: function (jobLabelModelHackDtos) {
                    buildLabelTree(jobLabelModelHackDtos);
                    populateDisplayLabels(root, 0);

                    self.reportLabelId(reportLabelId);

                    //This subscription is intentionally done here after setting the value above to avoid filter being called when the selected value is set
                    reportLabelIdSubscription = self.reportLabelId.subscribe(function(){
                        //If a filter is used, we should go to page 0 and start fresh
                        self.search("");
                        self.pageNumber(0);
                    });
                }
            })
        ).always(function(){
            waitingModal.hide();
        });

        self.searchAction = function() {
            //TODO - Hacky, needs to done better
            //If we do not do this, search variable gets set to empty string
            if (reportLabelIdSubscription !== null) {
                reportLabelIdSubscription.dispose();
            }

            self.reportLabelId(0);
            self.pageNumber(0);
            return false;
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
