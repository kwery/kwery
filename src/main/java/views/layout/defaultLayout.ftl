<#macro myLayout title="Layout example">
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="/assets/webjars/normalize.css/3.0.2/normalize.css" rel="stylesheet">
      <link href="/assets/webjars/skeleton-css/2.0.4/css/skeleton.css" rel="stylesheet">

    <!-- Latest compiled and minified JavaScript -->
    
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .error-template {padding: 40px 15px;text-align: center;}
    </style>

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  </head>
<body>

    <div class="container">
        
        <#include "header.ftl"/>
        
        <#if (flash.error)??>
            <div class="alert alert-danger">
                ${flash.error}
            </div>
        </#if>
        
        <#if (flash.success)??>
            <div class="alert alert-success">
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