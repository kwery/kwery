define(["jquery", "knockout", "crossroads", "hasher"], function ($, ko, crossroads, hasher) {

    return new Router({
        routes: [
            { url: "", params: { page: "onboardingWelcome" } },
            { url: "settings", params: { page: "settings" } },
            { url: "onboarding/add-admin-user", params: { page: "onboardingAddAdminUser" } },
            { url: "onboarding/add-datasource", params: { page: "onboardingAddDatasource" } }
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

