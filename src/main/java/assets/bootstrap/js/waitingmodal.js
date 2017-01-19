;((function(){
    define(["jquery", "bootstrap"], function($){
        function constructModal() {
            return $(
                '<div class="modal fade waiting-modal-f" data-backdrop="static" data-keyboard="false" id="waitingFor" tabindex="-1" role="dialog" aria-labelledby="waitingFor">' +
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
                if (typeof $modal === "undefined") {
                    $modal = constructModal();
                    $modal.modal();
                } else {
                    $modal.modal("show");
                }
            },
            hide: function() {
                if (typeof $modal !== "undefined") {
                    $modal.modal("hide");
                }
            }
        }
    });
})());

