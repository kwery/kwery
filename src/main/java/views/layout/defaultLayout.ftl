<#macro myLayout title="${title}">
    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="utf-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta name="userAuthenticated" content="${userAuthenticated}">

            <title>${title}</title>

            <link href="/assets/bootstrap/css/bootstrap.css" rel="stylesheet">
            <link href="/assets/css/custom.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.structure.css" rel="stylesheet">
            <link href="/assets/css/jquery-ui.theme.css" rel="stylesheet">

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