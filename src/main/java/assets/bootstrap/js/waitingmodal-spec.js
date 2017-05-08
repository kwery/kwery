define(['waitingmodal'], function(waitingModal){
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000;
    describe("Waiting Modal", function() {
        describe("Calling show and hide results in waiting modal being shown and hidden", function(){
            beforeEach(function(done){
                waitingModal.show(undefined, "test");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal.hide("test");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            afterEach(function(){
                expect($("#kweryModal").is(":visible")).toBe(false);
                waitingModal._cleanUp();
            });
        });
        describe("Calling show in succession and hide once results in waiting modal to be shown", function(){
            beforeEach(function(done){
                waitingModal.show(undefined, "test0");
                waitingModal.show(undefined, "test1");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal.hide("test0");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            afterEach(function(){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal._cleanUp();
            });
        });
        describe("Calling show, hide, show in succession results in waiting modals to be queued up", function(){
            beforeEach(function(done){
                waitingModal.show(undefined, "test0");
                waitingModal.hide("test0");
                waitingModal.show(undefined, "test1");
                setTimeout(function(){
                    done();
                }, 5000);
            });
            it("", function(){
                expect($("#kweryModal").is(":visible")).toBe(true);
            });
            afterEach(function(){
                waitingModal._cleanUp();
            });
        });
        describe("Calling show, hide, show, hide in succession results in clearing waiting modal", function(){
            beforeEach(function(done){
                waitingModal.show(undefined, "test0");
                waitingModal.hide("test0");
                waitingModal.show(undefined, "test1");
                waitingModal.hide("test1");
                setTimeout(function(){
                    done();
                }, 5000);
            });
            it("", function(){
                expect($("#kweryModal").is(":visible")).toBe(false);
            });
            afterEach(function(){
                waitingModal._cleanUp();
            });
        });
        describe("Calling show with a string results in loading with the string message", function(){
            beforeEach(function(done){
                waitingModal.show("foo", "test");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                expect($(".waiting-text-f").text().trim()).toEqual("foo");
                waitingModal.hide("test");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            afterEach(function(){
                expect($("#kweryModal").is(":visible")).toBe(false);
                waitingModal._cleanUp();
            });
        });
        describe("Calling show in succession results in second waiting modal to be shown", function(){
            beforeEach(function(done){
                waitingModal.show("first", "test0");
                waitingModal.show("second", "test1");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                expect($(".waiting-text-f").text().trim()).toEqual("second");
                waitingModal.hide("test0");
                setTimeout(function(){
                    done();
                }, 1000);
            });
            afterEach(function(){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal._cleanUp();
            });
        });
    });
});
