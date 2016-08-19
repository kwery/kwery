<div class="container">
    <actionresult params="status: status, message: message, isOpen: isOpen"></actionresult>
    <div class="row">
        <div class="twelve columns">
            <label for="url">${urlButtonM}</label>
            <input data-bind="value: url" class="u-full-width" type="text" id="url" name="url">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <label for="userName">${usernameButtonM}</label>
            <input data-bind="value: username" class="u-full-width" type="text" id="username" name="username">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <label for="password">${passwordButtonM}</label>
            <input data-bind="value: password" class="u-full-width" type="password" id="password" name="password">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <label for="label">${labelButtonM}</label>
            <input data-bind="value: label" class="u-full-width" type="text" id="label" name="label">
        </div>
    </div>
    <div class="row">
        <div class="twelve columns">
            <button data-bind="click: save" class="button-primary" id="create">${createButtonM}</button>
        </div>
    </div>
</div>
