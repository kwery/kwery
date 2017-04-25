define(['components/datasource/add', "knockout", "jasmine-extensions", "jasmine-mock-ajax"], function(obj, ko){
    describe("Datasource", function(){
        beforeEach(function(){
            jasmine.Ajax.install();
        });

        afterEach(function(){
            jasmine.Ajax.uninstall();
        });

        describe("Add Datasource", function(){
            beforeEach(function(){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;

                addDatasourceSuccessCbSpy = spyOn(obj.viewModel.prototype, "addDatasourceSuccessCb").and.callThrough();

                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
            });

            it("When the observables are updated, corresponding form fields get updated", function(){
                model.username("username");
                expect($(".username-f").val()).toEqual("username");

                model.password("password");
                expect($(".password-f").val()).toEqual("password");

                model.url("localhost");
                expect($(".url-f").val()).toEqual("localhost");

                model.showDatabase(true);
                model.database("foobarmoo");
                expect($(".database-f").val()).toEqual("foobarmoo");

                model.port(3306);
                expect($(".port-f").val()).toEqual("3306");

                model.label("label");
                expect($(".label-f").val()).toEqual("label");

                model.datasourceType("MYSQL");
                expect($(".type-f").val()).toEqual("MYSQL");
            });

            it("Database field is not shown when datasource type is MYSQL", function(){
                model.datasourceType("MYSQL");
                expect($(".database-group-f").is(":visible")).toBe(false);
            });

            it("Database field is shown when datasource type is Postgres", function(){
                model.datasourceType("POSTGRESQL");
                expect($(".database-group-f").is(":visible")).toBe(true);
            });

            it("Database field is shown when datasource type is Redshift", function(){
                model.datasourceType("REDSHIFT");
                expect($(".database-group-f").is(":visible")).toBe(true);
            });

            it("Database field is shown when datasource type is SQLSERVER", function(){
                model.datasourceType("SQLSERVER");
                expect($(".database-group-f").is(":visible")).toBe(true);
            });

            it("Validation errors are shown when required fields are not filled", function(){
                $(".save-datasource-f").submit();

                expect($(".type-error-f").text()).not.toEqual("");
                expect($(".username-error-f").text()).not.toEqual("");
                expect($(".url-error-f").text()).not.toEqual("");
                expect($(".port-error-f").text()).not.toEqual("");
                expect($(".password-error-f").text()).toEqual("");
                expect($(".label-error-f").text()).not.toEqual("");
                expect($(".database-error-f").text()).not.toEqual("");
            });

            it("Ajax request is made on datasource save", function(){
                $(".username-f").val("user").change();
                $(".password-f").val("password").change();
                $(".url-f").val("localhost").change();
                $(".database-f").val("foobarmoo").change();
                $(".port-f").val("5432").change();
                $(".label-f").val("postgres").change();
                $(".type-f").val("POSTGRESQL").change();

                $(".save-datasource-f").submit();

                var request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/datasource/add-datasource');
                expect(request.method).toBe('POST');

                expect(request.data()).toEqual({
                    url: "localhost",
                    port: 5432,
                    username: "user",
                    password: "password",
                    label: "postgres",
                    type: "POSTGRESQL",
                    database: "foobarmoo"
                });

                //Todo: Test callback
            });
        });

        describe("Update Datasource", function(){
            beforeEach(function(){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;

                populateFormSpy = spyOn(obj.viewModel.prototype, "populateForm").and.callThrough();

                model = new obj.viewModel({
                    datasourceId: 1
                });

                ko.applyBindings(model, testNode);
            });

            it("Form fields are filled with Ajax response on update", function(){
                var request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/datasource/1');
                expect(request.method).toBe('GET');

                var response = {
                    username: "user",
                    password: "test",
                    url: "localhost",
                    port: 3306,
                    label: "postgres",
                    type: "POSTGRESQL",
                    database: "foobarmoo"
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                expect(populateFormSpy).toHaveBeenCalledWith(1);

                expect($(".username-f").val()).toEqual("user");
                expect($(".password-f").val()).toEqual("test");
                expect($(".url-f").val()).toEqual("localhost");
                expect($(".database-f").val()).toEqual("foobarmoo");
                expect($(".port-f").val()).toEqual("3306");
                expect($(".label-f").val()).toEqual("postgres");
                expect($(".type-f").val()).toEqual("POSTGRESQL");
            });

            it("Form can be submitted after updating a datasource to one that does not have a database field", function(){
                var request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/datasource/1');
                expect(request.method).toBe('GET');

                var response = {
                    username: "user",
                    password: "test",
                    url: "localhost",
                    port: 5432,
                    label: "postgres",
                    type: "POSTGRESQL",
                    database: "foobarmoo"
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                $(".type-f").val("MYSQL").change();

                $(".save-datasource-f").submit();

                request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/datasource/add-datasource');
                expect(request.method).toBe('POST');

                expect(request.data()).toEqual({
                    id: 1,
                    url: "localhost",
                    port: 5432,
                    username: "user",
                    password: "test",
                    label: "postgres",
                    type: "MYSQL",
                    database: "foobarmoo"
                });
            });

            it("Form validation works after updating a datasource from non database type to database type and submitting an empty form", function(){
                var request = jasmine.Ajax.requests.mostRecent();

                var response = {
                    username: "user",
                    password: "test",
                    url: "localhost",
                    port: 3306,
                    label: "mysql",
                    type: "MYSQL"
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                $(".type-f").val("POSTGRESQL").change();

                $(".save-datasource-f").submit();

                expect($(".database-error-f").text()).not.toEqual("");
            });

            it("Form can be updated from non database type to database type", function(){
                var request = jasmine.Ajax.requests.mostRecent();

                var response = {
                    username: "user",
                    password: "test",
                    url: "localhost",
                    port: 3306,
                    label: "mysql",
                    type: "MYSQL"
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                $(".type-f").val("POSTGRESQL").change();
                $(".database-f").val("foobarmoo").change();

                $(".save-datasource-f").submit();

                request = jasmine.Ajax.requests.mostRecent();
                expect(request.data()).toEqual({
                    id: 1,
                    url: "localhost",
                    port: 3306,
                    username: "user",
                    password: "test",
                    label: "mysql",
                    type: "POSTGRESQL",
                    database: "foobarmoo"
                });
            });
        });
    });
});