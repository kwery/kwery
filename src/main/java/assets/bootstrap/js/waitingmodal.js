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

        var showQueue = [];
        var hideQueue = [];

        return {
            show : function(text) {
                var self = this;
                if (hideCalled === true || showCalled === true) {
                    showQueue.push({"show": text});
                } else {
                    showCalled = true;
                    $modal = constructModal(text);
                    $modal.modal();
                    $modal.on("hidden.bs.modal", function(){
                        $(this).remove();
                        hideCalled = false;
                        //Drain piled up events
                        if (showQueue.length > 0) {
                            self.show(showQueue.pop()["show"]);
                        }
                    });
                    $modal.on("shown.bs.modal", function(){
                        showCalled = false;
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
            }
        }
    });
})());

