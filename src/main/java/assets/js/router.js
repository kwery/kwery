define(["jquery", "knockout", "crossroads", "hasher"], function ($, ko, crossroads, hasher) {

    return new Router({
        routes: [
            { url: "", params: { page: "onboarding-welcome" } },
            { url: "settings", params: { page: "settings" } },
            { url: "onboarding/add-admin-user", params: { page: "onboarding-add-admin-user" } },
            { url: "onboarding/add-datasource", params: { page: "onboarding-add-datasource" } },
            { url: "user/login", params: { page: "user-login" } }
        ]
    });

    function Router(config) {
        var currentRoute = this.currentRoute = ko.observable({});

        ko.utils.arrayForEach(config.routes, function (route) {
            crossroads.addRoute(route.url, function (requestParams) {
                currentRoute(ko.utils.extend(requestParams, route.params));
            });
        });
        crossroads.routed.add(console.log, console);
        activateCrossroads();
    }

    function activateCrossroads() {
        function parseHash(newHash, oldHash){
            crossroads.parse(newHash);
        }
        crossroads.normalizeFn = crossroads.NORM_AS_OBJECT;

        hasher.initialized.add(parseHash);
        hasher.changed.add(parseHash);
        hasher.init();
    }
});

