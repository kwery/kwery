define(['waitingmodal'], function(waitingModal){
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 10000;
    describe("Waiting Modal", function() {
        describe("Calling show and hide results in waiting modal being shown and hidden", function(){
            beforeEach(function(done){
                waitingModal.show();
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal.hide();
                setTimeout(function(){
                    done();
                }, 1000);
            });
            afterEach(function(){
                expect($("#kweryModal").is(":visible")).toBe(false);
                waitingModal._cleanUp();
            });
        });
        describe("Calling show in succession results in waiting modals to be queued up", function(){
            beforeEach(function(done){
                waitingModal.show();
                waitingModal.show();
                setTimeout(function(){
                    done();
                }, 1000);
            });
            it("", function(done){
                expect($("#kweryModal").is(":visible")).toBe(true);
                waitingModal.hide();
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
                waitingModal.show();
                waitingModal.hide();
                waitingModal.show();
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
        describe("Calling show, hide, show, hide in succession results in waiting modals to be queued up", function(){
            beforeEach(function(done){
                waitingModal.show();
                waitingModal.hide();
                waitingModal.show();
                waitingModal.hide();
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
    });
});
