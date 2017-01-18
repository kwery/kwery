;((function(){
    define(["jquery", "bootstrap"], function($){
        function constructModal() {
            //Deleting previous incarnation
            if ($modal) {
                $modal.remove();
            }
            return $(
                '<div class="modal fade" data-backdrop="static" data-keyboard="false" id="waitingFor" tabindex="-1" role="dialog" aria-labelledby="waitingFor">' +
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
            },
            hide: function() {
                if (typeof $modal !== "undefined") {
                    $modal.modal("hide");
                }
            }
        }
    });
})());

