define(["jquery", "knockout", "crossroads", "hasher", "repo-dash"], function ($, ko, crossroads, hasher, repoDash) {
    //This is placed in router for now because there is no better place to place it for now.
    //Display the footer after 30 seconds, this is done not to cause a jarring affect during initial display of page.
    $(document).ready(function(){
        setTimeout(function(){
            $(".kwery-footer").show();
        }, 1000) //1 ms is a randomly chosen value
    });

    return new Router(repoDash.componentMapping.mapping());

    function Router(componentMapping) {
        var currentRoute = this.currentRoute = ko.observable({});

        ko.utils.arrayForEach(componentMapping, function (mapping) {
            var addedRoute = crossroads.addRoute(mapping.url, function (requestParams) {
                var routeObject = ko.utils.extend(requestParams, {page: mapping.component});
                currentRoute(routeObject);
            });

            addedRoute.rules = {
                request_ : function() {
                    if (mapping.auth && !repoDash.user.isAuthenticated()) {
                        return false;
                    }
                    return true;
                }
            };
        });

        //crossroads.routed.add(console.log, console);

        //If a route is bypassed, it means that it did not meet the auth rules, hence redirect to auth
        //Previous is appended so that post login intended page is shown
        crossroads.bypassed.add(function(){
            crossroads.parse("user/login");
        });

        activateCrossroads();
    }

    function activateCrossroads() {
        function parseHash(newHash, oldHash){
            crossroads.parse(newHash);
        }
        crossroads.normalizeFn = crossroads.NORM_AS_OBJECT;
        crossroads.shouldTypecast = true;

        //This is needed so that url parameters are not double encoded
        hasher.raw = true;
        hasher.initialized.add(parseHash);
        hasher.changed.add(parseHash);
        hasher.init();
    }
});

