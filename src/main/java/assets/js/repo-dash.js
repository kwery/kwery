;(function(){
    var factory = function($){
        var repoDash = (function(){
            var _userAuthenticated = false;

            if ($('meta[name="userAuthenticated"]').attr('content') === "true") {
                _userAuthenticated = true;
            }

            var user = {
                isAuthenticated: function() {
                    return _userAuthenticated;
                },
                setAuthenticated: function(authenticated) {
                    _userAuthenticated = authenticated;
                }
            };

            //Do not change this order unless you know what you are doing, changing the order may affect route resolution
            var _componentMapping = [
                {url: "", auth: false, component: "onboarding-welcome"},
                {url: "onboarding/add-admin-user", auth: false, component: "onboarding-add-admin-user"},
                {url: "onboarding/add-datasource", auth: true, component: "onboarding-add-datasource"},
                {url: "datasource/list", auth: true, component: "datasource-list"},
                {url: "datasource/{datasourceId}", auth: true, component: "onboarding-add-datasource"},
                {url: "user/login{?query}", auth: false, component: "user-login"},
                {url: "sql-query/add", auth: true, component: "sql-query-add"},
                {url: "sql-query/{sqlQueryId}", auth: true, component: "sql-query-add"},
                {url: "sql-query/executing", auth: true, component: "sql-query-executing"},
                {url: "sql-query/{sqlQueryId}/execution-list/:?q:", auth: true, component: "sql-query-execution-list"},
                {url: "sql-query/{sqlQueryId}/execution/{sqlQueryExecutionId}", auth: true, component: "sql-query-execution-result"},
                {url: "sql-query/list", auth: true, component: "sql-query-list"},
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
        define(["jquery"], factory);
    } else if (typeof exports === "object") {
        module.exports = factory(require("jquery"));
    } else {
        /*jshint sub:true */
        window["repoDash"] = factory(window["jquery"]);
    }
}());
