<#macro myLayout title="${title}">
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta name="userAuthenticated" content="${userAuthenticated}">

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
            <link href="/assets/css/jquery-ui.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.structure.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.theme.css" rel="stylesheet">

            <style type="text/css">
/*                #navbar {
                    background: white !important;
                }

                .navbar-default {
                    background: white !important;
                }*/

                .panel-heading {
/*                    background: #ffe169 !important;*/
                    background: #f4f9fb !important;
                }

                body {
/*                    background: #f4f9fb !important;*/
                    background: rgb(251, 249, 248) !important;

                }

                body {
                    font-family: 'Roboto', sans-serif !important;
                }

                .jumbotron {
                    background: white !important;
                }

                .top-buffer {
                    margin-top:20px;
                }
            </style>

            <!-- TODO Add all the required IE8 and other stuff for bootstrap -->
        </head>
        <body>
            <nav-bar></nav-bar>
            <#nested/>
        </body>
        <script src="/assets/app/require.config.js"></script>
        <script data-main="startup" src="/assets/js/require.js"></script>
        <#--    TODO - Fix this, using deprecated directive-->
        <#noescape>
            <script type="text/javascript">
                var dashRepoMessages = ${allMessages};
            </script>
        </#noescape>
    </html>
</#macro>