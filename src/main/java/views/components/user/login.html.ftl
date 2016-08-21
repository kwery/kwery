<div class="container">
    <actionresult params="status: status, message: message"></actionresult>
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
            <button data-bind="click: login" class="button-primary" id="login">${loginButtonM}</button>
        </div>
    </div>
</div>
