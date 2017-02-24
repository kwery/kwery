<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout>
<div id="page" class="container" style="padding-top:10px" data-bind="component: { name: route().page, params: route }">
</div>
</@layout.myLayout>
