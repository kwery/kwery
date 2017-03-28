<#macro myLayout title="${title}">
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta name="userAuthenticated" content="${userAuthenticated}">

            <link rel="icon" href="/assets/images/favicon.ico" type="image/x-icon" />

            <title>${title}</title>

            <style type="text/css">
                @font-face {
                    font-family: 'Roboto';
                    font-weight: 400;
                    font-style: normal;
                    src: url('/assets/fonts/Roboto-regular/Roboto-regular.eot');
                    src: url('/assets/fonts/Roboto-regular/Roboto-regular.eot?#iefix') format('embedded-opentype'),
                    local('Roboto'),
                    local('Roboto-regular'),
                    url('/assets/fonts/Roboto-regular/Roboto-regular.woff2') format('woff2'),
                    url('/assets/fonts/Roboto-regular/Roboto-regular.woff') format('woff'),
                    url('/assets/fonts/Roboto-regular/Roboto-regular.ttf') format('truetype'),
                    url('/assets/fonts/Roboto-regular/Roboto-regular.svg#Roboto') format('svg');
                }
            </style>

            <link href="/assets/bootstrap/css/bootstrap.css" rel="stylesheet">
            <link href="/assets/css/custom.css" rel="stylesheet">
            <link href="/assets/bootstrap/css/datetimepicker.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.structure.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.theme.css" rel="stylesheet">

            <style type="text/css">
                #navbar {
                    background: white !important;
                }

                .navbar-default {
                    background: white !important;
                }

                .panel-heading {
                    background: #f4f9fb !important;
                }

                body {
                    background: #f0f4f5 !important;
                    font-family: 'Roboto', sans-serif !important;
                }

                .jumbotron {
                    background: white !important;
                }

                .modal {
                    position: fixed;
                    top: 50% !important;
                    left: 50%;
                }

                .kwery-color {
                    color: #1f3a93;
                }
            </style>

            <!-- TODO Add all the required IE8 and other stuff for bootstrap -->
        </head>
        <body>
            <license-details></license-details>
            <nav-bar></nav-bar>
            <#nested/>
            <div class="kwery-footer" style="display: none">
                <hr>
                <div class="text-center text-muted">
                    <a href="http://getkwery.com" target="kwery">Kwery</a> - The simplest way to generate, schedule, view and email business reports from datasources using SQL.
                </div>
                <br>
            </div>
        </body>
        <script src="/assets/app/require.config.js"></script>
        <script data-main="startup" src="/assets/js/require.js"></script>
    </html>
</#macro>