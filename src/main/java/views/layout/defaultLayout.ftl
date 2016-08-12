<#macro myLayout title="Layout example">
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <link href="/assets/webjars/normalize.css/3.0.2/normalize.css" rel="stylesheet">
    <link href="/assets/webjars/skeleton-css/2.0.4/css/skeleton.css" rel="stylesheet">
      <link href="/assets/css/custom.css" rel="stylesheet">

  </head>
<body>
    <div class="container">
        <#include "header.ftl"/>
        
        <#if (flash.error)??>
            <div class="isa_error">
                ${flash.error}
            </div>
        </#if>
        
        <#if (flash.success)??>
            <div class="isa_info">
                ${flash.success}
            </div>
        </#if>

        <#nested/>

        <#include "footer.ftl"/>
    </div> <!-- /container -->
    <script type="text/javascript" src="/assets/webjars/jquery/3.1.0/jquery.js"></script>
</body>
</html>
</#macro>