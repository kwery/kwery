<div class="container">
    <action-result params="status: status,
                            message: message,
                            onSuccessShowDialog: true,
                            nextAction: '${nextAction}',
                            nextActionName: '${nextActionName}'">
    </action-result>
    <form data-bind="submit: submit">
        <div class="row">
            <div class="six columns">
                <label data-bind="text: ko.i18n('user.name')" for="userName"></label>
                <input data-bind="value: username" class="u-full-width" type="text" id="username" name="username">
            </div>
            <div class="six columns">
                <label data-bind="text: ko.i18n('password')" for="password"></label>
                <input data-bind="value: password" class="u-full-width" type="password" id="password" name="password">
            </div>
        </div>
        <div class="row">
            <div class="twelve columns">
                <button data-bind="text: ko.i18n('create')" class="button-primary" id="createAdminUser"></button>
            </div>
        </div>
    </form>
</div>
