;((function(){
    define(["jquery", "waitingmodal"], function($, waitingModal){
        return {
            waitingAjax: function(jqueryAjaxObj, id) {
                jqueryAjaxObj['beforeSend'] = function() {
                    waitingModal.show(undefined, id);
                };

                $.ajax(jqueryAjaxObj).always(function(){
                    waitingModal.hide(id);
                });
            }
        }
    });
})());
