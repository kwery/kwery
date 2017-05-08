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

        //Tracks whether modal is open
        var modalShown = false;

        return {
            show : function(text, id) {
                var self = this;
                //At a time only one modal can exist
                //Modals do not open or close immediately on call, there is a delay due to the transition effect
                //If a modal is already being show, close it before opening a new one
                if (modalShown === true) {
                    this._cleanUp();
                }

                modalShown = true;

                $modal = constructModal(text);
                $modal.modal();
                $modal.data("namespace", id);

                $modal.on("hidden.bs.modal", function(){
                    modalShown = false;
                    $modal.remove();
                });
            },
            hide: function(id) {
                //Ensure that the modal for which hide has been called is still around
                if ($modal.data("namespace") === id) {
                    $modal.modal("hide");
                }
            },
            //For testing
            _cleanUp: function() {
                $modal.removeClass("fade");
                $modal.off("hidden.bs.modal");
                $modal.modal('hide');
                $(".modal-backdrop").remove();
                $modal.remove();
            }
        }
    });
})());

