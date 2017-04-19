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
        "waitingmodal": "/assets/bootstrap/js/waitingmodal",
        "ajaxutil": "ajaxutil",
        "jstorage": "jstorage",
        "messages": "/messages",
        "jasmine": "/assets/jasmine/jasmine",
        "jasmine-html": "/assets/jasmine/jasmine-html",
        "jasmine-boot": "/assets/jasmine/boot",
        "jasmine-extensions": "/assets/jasmine/jasmine-extensions",
        "jasmine-mock-ajax": "/assets/jasmine/mock-ajax"
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
        "waitingmodal": {
            deps: ["bootstrap", "jquery"]
        },
        "ajaxutil": {
            deps: ["jquery", "waitingmodal"]
        },
        "jstorage": {
            deps: ["jquery"],
            exports: "jstorage"
        },
        "jasmine-html": {
            deps: ["jasmine"]
        },
        "jasmine-boot": {
            deps: ["jasmine", "jasmine-html"]
        },
        "jasmine-mock-ajax": {
            deps: ["jasmine"]
        }
    }
};
