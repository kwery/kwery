<div class="row">
    <div class="twelve columns">
        <div data-bind="visible: successMessage().length > 0" class="isa_info">
            <p data-bind="text: successMessage"></p>
        </div>

        <div data-bind="visible: failureMessage().length > 0" class="isa_error">
            <p data-bind="text: failureMessage"></p>
        </div>
    </div>
</div>

