define(['components/logo/save', "knockout", "jasmine-extensions", "jasmine-mock-ajax"], function(obj, ko){
    describe("Logo", function(){
        describe("Update logo", function(){
            beforeEach(function(){
                jasmine.Ajax.install();
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
            });

            afterEach(function(){
                jasmine.Ajax.uninstall();
            });

            it("Form fields are filled with ajax response", function(){
                var request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/report-email-configuration/get');
                expect(request.method).toBe('GET');

                var response = {
                    logoUrl: "https://s3.amazonaws.com/getkwery.com/logo.png",
                    id: 1
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                expect($(".logo-f").val()).toEqual("https://s3.amazonaws.com/getkwery.com/logo.png");
                expect($(".logo-img-f").attr("src")).toEqual("https://s3.amazonaws.com/getkwery.com/logo.png");
                expect($(".logo-img-f").is(":visible")).toBe(true);

                expect(model.id()).toEqual(1);
            });
        });

        describe("Add/update logo", function() {
            beforeEach(function(){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                submitFormSpy = spyOn(obj.viewModel.prototype, "submitForm");
                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
            });

            it("Validation errors are shown when required fields are not filled", function(){
                $(".save-logo-f").submit();

                expect($(".logo-error-f").text()).not.toEqual("");
                expect(submitFormSpy).not.toHaveBeenCalled();
            });

            it("Logo is not visible when logo url field is empty", function(){
                expect($(".logo-img-f").is(":visible")).toBe(false);
            });

            it("Logo is visible when logo url field is filled", function(){
                $(".logo-f").val("https://s3.amazonaws.com/getkwery.com/logo.png").change();
                expect($(".logo-img-f").attr("src")).toEqual("https://s3.amazonaws.com/getkwery.com/logo.png");
                expect($(".logo-img-f").is(":visible")).toBe(true);
            });
        });

        describe("Update logo", function(){
            beforeEach(function(done){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                model = new obj.viewModel();
                model.formSubmissionSuccessMessage();
                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
                setTimeout(function(){
                    done();
                }, 1000);
            });

            it("On update, success message is displayed", function(){
                expect($(".f-success-message").text().trim()).toEqual("Logo image URL saved successfully");
            });
        });

        describe("Save logo", function(){
            beforeEach(function(){
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
                submitFormCbSpy = spyOn(obj.viewModel.prototype, "submitFormCb");
                spyOn($, 'ajax').and.callFake(function (req) {
                    var d = $.Deferred();
                    d.resolve({
                        status: "success"
                    });
                    return d.promise();
                });
            });

            it("On form submission, form is submitted through ajax and response callback is called", function(){
                $(".logo-f").val("https://s3.amazonaws.com/getkwery.com/logo.png").change();
                $(".save-logo-f").submit();
                expect(submitFormCbSpy).toHaveBeenCalled();
            });
        });

        describe("Save logo", function(){
            beforeEach(function(){
                jasmine.Ajax.install();
                jasmine.prepareTestNode();
                testNode.innerHTML = obj.template;
                submitFormCbSpy = spyOn(obj.viewModel.prototype, "submitFormCb");
                model = new obj.viewModel();
                ko.applyBindings(model, testNode);
            });

            afterEach(function(){
                jasmine.Ajax.uninstall();
            });

            it("On form submission, data is sent to server through ajax call", function(){
                $(".logo-f").val("https://s3.amazonaws.com/getkwery.com/logo.png").change();
                model.id(1);

                $(".save-logo-f").submit();

                var request = jasmine.Ajax.requests.mostRecent();

                expect(request.url).toBe('/api/report-email-configuration/save');
                expect(request.method).toBe('POST');
                expect(request.data()).toEqual({
                    logoUrl: "https://s3.amazonaws.com/getkwery.com/logo.png",
                    id: 1
                });

                var response = {
                    status: "success"
                };

                request.respondWith({
                    status: 200,
                    responseText: JSON.stringify(response)
                });

                expect(submitFormCbSpy).toHaveBeenCalled();
            });
        });
    });
});
