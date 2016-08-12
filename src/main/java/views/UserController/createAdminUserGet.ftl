<#import "../layout/defaultLayout.ftl" as layout>
<@layout.myLayout "Create Admin User">

<form method="post">
    <div class="container">
        <div class="row">
            <div class="six columns">
                <label for="userName">${i18n("user.name")}</label>
                <input class="u-full-width" type="text" id="username" name="username">
            </div>
            <div class="six columns">
                <label for="password">${i18n("password")}</label>
                <input class="u-full-width" type="password" id="password" name="password">
            </div>
        </div>
        <div class="row">
            <div class="twelve columns">
                <button class="button-primary">${i18n("create")}</button>
            </div>
        </div>
    </div>
</form>

</@layout.myLayout>
