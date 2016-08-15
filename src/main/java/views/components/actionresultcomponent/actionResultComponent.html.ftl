<div class="row">
    <div class="twelve columns">
        <div data-bind="visible: status() === 'success'" class="isa_info">
            <p data-bind="text: message"></p>
        </div>

        <div data-bind="visible: status() === 'failure'" class="isa_error">
            <p data-bind="text: message"></p>
        </div>
    </div>
</div>

