;((function(){
    define(["jquery", "bootstrap"], function($){
        function constructModal(text) {
            var html = '<div class="modal fade waiting-modal-f" id="kweryModal" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="waitingFor">' +
                '<div class="modal-dialog" role="document">' +
                '<div>';

            if (text === undefined) {
                html = html + '<img class="center-block" src="/assets/images/loader.gif">';
            } else {
                html = html + '<div class="text-center">' +
                    '<div class="progress center-block" style="width:300px;">' +
                    '<div class="progress-bar progress-bar-striped active" role="progressbar" style="width: 100%;">' +
                    '<span class="waiting-text-f">' +
                    text +
                    '</span>' +
                    '</div>' +
                    '</div>' +
                    '</div>';
            }

            html = html + '</div>' +
                '</div>' +
                '</div>';
            return $(html);
        }

        var $modal;

        //Tracks the time between calling action and the actions taking place post CSS transitions
        var showCalled = false;
        var hideCalled = false;
        //Tracks whether modal is open
        var modalShown = false;

        var showQueue = [];
        var hideQueue = [];

        return {
            show : function(text) {
                var self = this;
                //At a time only one modal can exist
                //Modals do not open or close immediately on call, there is a delay due to the transition effect
                if (hideCalled === true || showCalled === true || modalShown === true) {
                    showQueue.push({"show": text});
                } else {
                    showCalled = true;
                    $modal = constructModal(text);
                    $modal.modal();
                    $modal.on("hidden.bs.modal", function(){
                        $(this).remove();
                        hideCalled = false;
                        modalShown = false;
                        //Drain piled up events
                        if (showQueue.length > 0) {
                            self.show(showQueue.pop()["show"]);
                        }
                    });
                    $modal.on("shown.bs.modal", function(){
                        showCalled = false;
                        modalShown = true;
                        //Drain piled up events
                        if (hideQueue.length > 0) {
                            hideQueue.pop();
                            self.hide();
                        }
                    });
                }
            },
            hide: function() {
                if (hideCalled === true || showCalled === true) {
                    hideQueue.push("hide");
                } else {
                    hideCalled = true;
                    $modal.modal("hide");
                }
            },
            //For testing
            _cleanUp: function() {
                $("#kweryModal").removeClass("fade");
                $modal.off("hidden.bs.modal");
                $modal.modal("hide");
                $modal.remove();
                showCalled = false;
                hideCalled = false;
                modalShown = false;
                showQueue = [];
                hideQueue = [];
            }
        }
    });
})());

