<div class="container">
    <actionresult params="status: status, message: message"></actionresult>
    <div class="row">
        <div class="six columns">
            <label for="userName">${i18n("user.name")}</label>
            <input data-bind="value: username" class="u-full-width" type="text" id="username" name="username">
        </div>
        <div class="six columns">
            <label for="password">${i18n("password")}</label>
            <input data-bind="value: password" class="u-full-width" type="password" id="password" name="password">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <button data-bind="click: save" class="button-primary" id="createAdminUser">${i18n("create")}</button>
        </div>
    </div>
</div>
