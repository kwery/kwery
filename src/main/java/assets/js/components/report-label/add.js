define(["knockout", "jquery", "text!components/report-label/add.html", "validator"], function (ko, $, template) {
    function viewModel(params) {
        var self = this;

        self.status = ko.observable("");
        self.messages = ko.observableArray([]);

        self.labelName = ko.observable();

        var Node = function(label, id) {
            this.label = label;
            this.id = id;
            this.children = [];

            this.addChild = function(node) {
                this.children.push(node);
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
        var root = new Node("", 0);

        var DisplayLabel = function(id, label) {
            this.id = id;
            this.label = label;
        };

        self.displayLabels = ko.observableArray([new DisplayLabel("", "")]);
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

        //Get current labels
        $.ajax({
            url: "/api/job-label/list",
            type: "GET",
            contentType: "application/json",
            success: function (jobLabelModelHackDtos) {
                //Create tree using labels
                ko.utils.arrayForEach(jobLabelModelHackDtos, function (jobLabelModelHackDto) {
                    var parent = jobLabelModelHackDto.parentJobLabelModel;
                    var child = jobLabelModelHackDto.jobLabelModel;

                    if (parent != undefined) {
                        //Label has a parent label
                        var parentNodeInTree = findNode(root, parent.id);
                        if (parentNodeInTree !== undefined) {
                            //Parent label is already present in the tree, hence add the child label as a child of the existing parent
                            parentNodeInTree.addChild(new Node(child.label, child.id));
                        } else {
                            //Parent label is not present in the tree, create a new parent node
                            var parentNode = new Node(parent.label, parent.id);
                            //Add the child label as a child of the parent node
                            parentNode.addChild(new Node(child.label, child.id));
                            //Add parent node as a child of the root label
                            root.addChild(parentNode);
                        }
                    } else {
                        //Label does not have a parent
                        var nodeInTree = findNode(root, child.id);
                        if (nodeInTree === undefined) {
                            //Node is not present in the tree, hence create a new label node and add it as child of the root node
                            root.addChild(new Node(child.label, child.id));
                        } else {
                            //Label is already present in the tree. Added as the parent of some other label, no action to take
                        }
                    }
                });

                //Add spaces and construct a tree like structure to show in select drop down
                var formatLabels = function(node, count) {
                    if (node.id !== 0) {
                        var label = Array(count).join("&nbsp;&nbsp;&nbsp;&nbsp;") + node.label;
                        self.displayLabels.push(new DisplayLabel(node.id, label));
                    }
                    $.each(node.children, function(index, node) {
                        return formatLabels(node, count + 1);
                    });
                };

                formatLabels(root, 0);
            }
        });

        self.validate = function() {
            $("#labelForm").validator({
                disable: false
            }).on("submit", function (e) {
                if (!e.isDefaultPrevented()) {//Valid form
                    $.ajax({
                        url: "/api/job-label/save",
                        type: "POST",
                        data: ko.toJSON({
                            labelName: self.labelName(),
                            parentLabelId: self.parentLabelOpted() ? self.parentLabelId() : 0
                        }),
                        contentType: "application/json",
                        success: function(result) {
                            self.status(result.status);
                            self.messages([ko.i18n('report.label.save.success', {0: self.labelName()})]);
                        }
                    });
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
