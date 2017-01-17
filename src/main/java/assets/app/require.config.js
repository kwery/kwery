var require = {
    baseUrl: "/assets/js",
    paths: {
        "crossroads": "crossroads",
        "jquery": "jquery",
        "knockout": "knockout",
        "knockout-projections": "knockout-projections",
        "signals": "signals",
        "hasher": "hasher",
        "text": "requirejs-text",
        "repo-dash": "repo-dash",
        "router": "router",
        "polyglot": "polyglot",
        "bootstrap": "/assets/bootstrap/js/bootstrap",
        "validator": "/assets/bootstrap/js/validator",
        "jquery-cron": "jquery-cron",
        "moment": "moment",
        "datetimepicker": "/assets/bootstrap/js/datetimepicker",
        "waitingfor": "/assets/bootstrap/js/waitingfor"
    },
    shim: {
        "bootstrap": {
            deps: ["jquery"]
        },
        "validator": {
            deps: ["jquery"],
            exports: '$.fn.validator'
        },
        "jquery-cron": {
            deps: ["jquery"]
        },
        "datetimepicker": {
            deps: ["moment", "bootstrap", "jquery"]
        },
        "waitingfor": {
            deps: ["bootstrap", "jquery"]
        }
    }
};
