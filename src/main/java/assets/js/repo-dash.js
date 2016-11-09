;(function(){
    var factory = function($, ko){
        var repoDash = (function(){
            var _userAuthenticated = false;

            if ($('meta[name="userAuthenticated"]').attr('content') === "true") {
                _userAuthenticated = true;
            }

            var user = {
                isAuthenticated: function() {
                    return _userAuthenticated;
                },
                userAuthenticationBroadcaster: new ko.subscribable(),
                setAuthenticated: function(authenticated) {
                    _userAuthenticated = authenticated;
                    user.userAuthenticationBroadcaster.notifySubscribers(authenticated, "userLogin");
                }
            };

            //Do not change this order unless you know what you are doing, changing the order may affect route resolution
            var _componentMapping = [
                {url: "", auth: false, component: "onboarding"},
                {url: "onboarding", auth: true, component: "onboarding"},
                {url: "user/add", auth: true, component: "user-add"},
                {url: "datasource/add", auth: true, component: "datasource-add"},
                {url: "datasource/list", auth: true, component: "datasource-list"},
                {url: "datasource/{datasourceId}", auth: true, component: "datasource-add"},
                {url: "user/login{?query}", auth: false, component: "user-login"},
                {url: "sql-query/add", auth: true, component: "sql-query-add"},
                {url: "sql-query/executing", auth: true, component: "sql-query-executing"},
                {url: "sql-query/{sqlQueryId}/execution-list/:?q:", auth: true, component: "sql-query-execution-list"},
                {url: "sql-query/{sqlQueryId}/execution/{sqlQueryExecutionId}", auth: true, component: "sql-query-execution-result"},
                {url: "sql-query/list", auth: true, component: "sql-query-list"},
                {url: "sql-query/{sqlQueryId}", auth: true, component: "sql-query-add"},
                {url: "user/list", auth: true, component: "user-list"},
                {url: "user/update/{userId}", auth: true, component: "user-update"},
            ];

            var componentMapping = {
                mapping: function() {
                    return _componentMapping;
                },
                component: function(url) {
                    for (var i = 0; i < _componentMapping.length; ++i) {
                        var mapping = _componentMapping[i];
                        if (mapping.url === url) {
                            return mapping.component;
                        }
                    }

                    return null;
                }
            };

            var repoDash = {
                "user": user,
                "componentMapping": componentMapping
            };
            return repoDash;
        })();

        return repoDash;
    };

    if (typeof define === "function" && define.amd) {
        define(["jquery", "knockout"], factory);
    } else if (typeof exports === "object") {
        module.exports = factory(require("jquery", "knockout"));
    } else {
        /*jshint sub:true */
        window["repoDash"] = factory(window["jquery"], window["knockout"]);
    }
}());
