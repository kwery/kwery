define(['components/report/add', "knockout", "jasmine-extensions", "jasmine-mock-ajax"], function(obj, ko){
    debugger;
    describe("Report", function(){
        beforeEach(function(){
            jasmine.Ajax.install();
        });

        afterEach(function(){
            jasmine.Ajax.uninstall();
        });

        describe("Parameter report", function(){
            beforeEach(function(){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                model = new obj.viewModel({});
                ko.applyBindings(model, testNode);
            });

            it("Parameter CSV field shows up when a parameterised SQL query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table where column = :column").change();
                expect($(".parameters-csv-f").is(":visible")).toBe(true);
            });

            it("Parameter CSV field shows up when a parameterised and non parameterised SQL query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table where column = :column").change();
                $(".f-add-sql-query").click();
                $(".f-sql-query1 .f-query").val("select * from table").change();
                expect($(".parameters-csv-f").is(":visible")).toBe(true);
            });

            it("Parameter CSV field does not show up when a non parameterised SQL query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table").change();
                expect($(".parameters-csv-f").is(":visible")).toBe(false);
            });

            it("Parameter CSV is a mandatory field", function(){
                $(".f-sql-query0 .f-query").val("select * from table where column = :column").change();
                $(".f-report-submit").submit();
                expect($(".parameter-csv-error-f").text()).not.toEqual("");
            });

            it("Only save button is shown when a parameterised query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table where column = :column").change();
                expect($(".f-report-submit").is(":visible")).toBe(true);
                expect($(".f-report-generate-submit").is(":visible")).toBe(false);
            });

            it("Only save button is shown when a parameterised query and non parameterised query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table where column = :column").change();
                $(".f-add-sql-query").click();
                $(".f-sql-query1 .f-query").val("select * from table").change();
                expect($(".f-report-submit").is(":visible")).toBe(true);
                expect($(".f-report-generate-submit").is(":visible")).toBe(false);
            });

            it("Both save and generate buttons are shown when a non parameterised query is entered", function(){
                $(".f-sql-query0 .f-query").val("select * from table").change();
                expect($(".f-report-submit").is(":visible")).toBe(true);
                expect($(".f-report-generate-submit").is(":visible")).toBe(true);
            });
        });
    })
});
