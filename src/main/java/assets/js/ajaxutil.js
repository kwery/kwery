;((function(){
    define(["jquery", "waitingmodal"], function($, waitingModal){
        return {
            waitingAjax: function(jqueryAjaxObj) {
                jqueryAjaxObj['beforeSend'] = function() {
                    waitingModal.show();
                };

                $.ajax(jqueryAjaxObj).always(function(){
                    waitingModal.hide();
                });
            }
        }
    });
})());
