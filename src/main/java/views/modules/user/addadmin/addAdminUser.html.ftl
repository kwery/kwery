<div class="container">
    <action-result params="status: status,
                            message: message,
                            onSuccessShowDialog: true,
                            nextAction: '${nextAction}',
                            nextActionName: '${nextActionName}'">
    </action-result>
    <div class="row">
        <div class="six columns">
            <label for="userName">${usernameButtonM}</label>
            <input data-bind="value: username" class="u-full-width" type="text" id="username" name="username">
        </div>
        <div class="six columns">
            <label for="password">${passwordButtonM}</label>
            <input data-bind="value: password" class="u-full-width" type="password" id="password" name="password">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <button data-bind="click: save" class="button-primary" id="createAdminUser">${createButtonM}</button>
        </div>
    </div>
</div>
