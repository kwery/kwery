;((function(){
    define(["jquery", "bootstrap"], function($){
        function constructModal(text) {
            var html = '<div class="modal fade waiting-modal-f" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="waitingFor">' +
                '<div class="modal-dialog" role="document">' +
                '<div>';

            if (text === undefined) {
                html = html + '<img class="center-block" src="/assets/images/loader.gif">';
            } else {
                html = html + '<div class="text-center">' +
                    '<div class="progress center-block" style="width:200px;">' +
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

        return {
            show : function(text) {
                $modal = constructModal(text);
                $modal.modal();
                $modal.on("hidden.bs.modal", function(){
                    $(this).remove();
                });
            },
            hide: function() {
                $modal.modal("hide");
            }
        }
    });
})());

