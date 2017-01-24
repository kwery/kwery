;((function(){
    define(["jquery", "bootstrap"], function($){
        function constructModal() {
            return $(
                '<div class="modal fade waiting-modal-f" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-labelledby="waitingFor">' +
                '<div class="modal-dialog" role="document">' +
                '<div>' +
                '<img src="/assets/images/loader.gif">' +
                '</div>' +
                '</div>' +
                '</div>'
            );
        }

        var $modal;

        return {
            show : function() {
                $modal = constructModal();
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

