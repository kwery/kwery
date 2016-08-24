<#macro myLayout title="${title}">
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <meta name="userAuthenticated" content="${userAuthenticated}">

    <link href="/assets/css/normalize.css" rel="stylesheet">
    <link href="/assets/css/skeleton.css" rel="stylesheet">
    <link href="/assets/css/custom.css" rel="stylesheet">
    <link href="/assets/css/jquery-ui.css" rel="stylesheet">
    <link href="/assets/css/jquery-ui.structure.css" rel="stylesheet">
    <link href="/assets/css/jquery-ui.theme.css" rel="stylesheet">

</head>
<body>
<div class="container">
    <#include "header.ftl"/>

    <#nested/>

    <#include "footer.ftl"/>
</div> <!-- /container -->
</body>
    <script src="/assets/app/require.config.js"></script>
    <script data-main="js/startup" src="/assets/js/require.js"></script>
</html>
</#macro>