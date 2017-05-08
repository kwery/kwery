define(["knockout", "jquery", "text!components/report-label/list.html", "ajaxutil", "jstorage"], function (ko, $, template, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        //To show save messages
        var status = $.jStorage.get("reportLabel:status", null);
        self.status = ko.observable("");
        if (status != null) {
            self.status(status);
            $.jStorage.deleteKey("reportLabel:status");
        }

        var messages = $.jStorage.get("reportLabel:messages", null);
        self.messages = ko.observableArray([]);
        if (messages != null) {
            self.messages(messages);
            $.jStorage.deleteKey("reportLabel:messages");
        }

        //TODO - Duplicated code with add label page, needs to be refactored into a common code
        //Label related - start
        var Node = function(label, id, parent) {
            this.label = label;
            this.id = id;
            this.children = [];
            this.parent = parent;

            this.addChild = function(node) {
                node.parent = this;
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

        self.displayLabels = ko.observableArray([]);

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
        ajaxUtil.waitingAjax({
            url: "/api/job-label/list",
            type: "GET",
            contentType: "application/json",
            success: function (jobLabelModelHackDtos) {
                buildLabelTree(jobLabelModelHackDtos);
                populateDisplayLabels(root, 0);
            }
        }, "getAllJobLabels");
        //Label related - end

        self.delete = function(displayLabel) {
            ajaxUtil.waitingAjax({
                url: "/api/job-label/delete/" + displayLabel.id,
                type: "POST",
                contentType: "application/json",
                success: function(actionResult) {
                    self.status(actionResult.status);
                    self.messages(actionResult.messages);
                    if (self.status() === "success") {
                        self.displayLabels.remove(displayLabel);
                    }
                }
            }, "deleteJobLabel");
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
