define(["knockout", "jquery", "text!components/report-label/add.html", "waitingmodal", "ajaxutil", "jstorage", "validator"], function (ko, $, template, waitingModal, ajaxUtil) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.labelId = ko.observable(0);

        var isUpdate = params.reportLabelId !== undefined;
        if (isUpdate) {
            self.labelId(params.reportLabelId);
        }

        self.labelName = ko.observable();

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
        self.parentLabelId = ko.observable();
        self.parentLabelOpted = ko.observable(false);

        self.parentLabelOpted.subscribe(function(opted){
            $("#parentLabelId").attr("data-validate", opted);
            /**
             * For scenario:
             * 1. User chooses to nest the label.
             * 2. Saved without using a parent label.
             * 3. Invalid parent label messages is shown.
             * 4. User un checks nest under check box.
             * 5. Saved parent label.
             *
             * If we do not do the below, validation message associated with the parent lable is still shown.
             */
            if (!opted) {
                //To remove any existing validation messages associated with parent label
                $("#labelForm").validator("destroy");
                self.validate();
                //Validation messages associated with actually invalid elements should still be shown
                $("#labelForm").validator("validate");
            } else {
                self.refreshValidation();
            }
        });

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
        $.when(
            $.ajax({
                url: "/api/job-label/list",
                type: "GET",
                contentType: "application/json",
                beforeSend: function(){
                    waitingModal.show();
                },
                success: function (jobLabelModelHackDtos) {
                    buildLabelTree(jobLabelModelHackDtos);

                    //Remove self(and it's children) from label node tree because a label or it's child labels cannot become a parent of itself
                    if (isUpdate) {
                        var labelNode = findNode(root, self.labelId());
                        labelNode.remove();
                    }

                    populateDisplayLabels(root, 0);
                }
            })
        ).then(function(){
            if (isUpdate) {
                return $.ajax({
                    url: "/api/job-label/" + self.labelId(),
                    type: "GET",
                    contentType: "application/json",
                    success: function(model) {
                        if (isUpdate) {
                            self.labelName(model.jobLabelModel.label);
                            if (model.parentJobLabelModel !== null) {
                                self.parentLabelOpted(true);
                                //This should happen only post population of display labels
                                self.parentLabelId(model.parentJobLabelModel.id);
                            }
                        }
                    }
                })
            }
        }).always(function(){
            waitingModal.hide();
            self.refreshValidation();
        });

        self.validate = function() {
            $("#labelForm").validator({
                disable: false
            }).on("submit", function (e) {
                if (!e.isDefaultPrevented()) {//Valid form
                    ajaxUtil.waitingAjax({
                        url: "/api/job-label/save",
                        type: "POST",
                        data: ko.toJSON({
                            labelName: self.labelName(),
                            labelId: self.labelId(),
                            parentLabelId: self.parentLabelOpted() ? self.parentLabelId() : 0,
                        }),
                        contentType: "application/json",
                        success: function(result) {
                            if (result.status === "success") {
                                if ($.jStorage.storageAvailable()) {
                                    $.jStorage.set("reportLabel:status", result.status, {TTL: (10 * 60 * 1000)});
                                    $.jStorage.set("reportLabel:messages", [ko.i18n('report.label.save.success', {0: self.labelName()})], {TTL: (10 * 60 * 1000)});
                                    window.location.href = "#report-label/list";
                                } else {
                                    throw new Error("Not enough space available to store result in browser");
                                }
                            } else {
                                self.status(result.status);
                                self.messages(result.messages);
                            }
                        }
                    })
                }

                return false;
            });
        };

        self.validate();

        self.refreshValidation = function() {
            $("#labelForm").validator("update");
        };

        return self;
    }
    return { viewModel: viewModel, template: template };
});
