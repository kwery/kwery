<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout "Home page">

<div id="page" class="container" style="padding-top:50px" data-bind="component: { name: route().page, params: route }">
</div>

</@layout.myLayout>
