<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout "Home page">

<div class="container">
    <div class="row">
        <div class="twelve columns">
            <h1>
                ${i18n("installation.welcome")}
            </h1>
            <a class="button button-primary" href="${path}">${i18n("create.admin.user")}</a>
        </div>
    </div>
</div>

</@layout.myLayout>