<div class="row">
    <div class="twelve columns">
        <div id="actionResultDialog" data-bind="dialog: {isOpen: isOpen}">
            <p data-bind="text: message"></p>
            <a data-bind="text: nextActionName, attr: {href: nextAction}" id="nextAction"></a>
        </div>

        <div data-bind="visible: status() === 'failure'" class="isa_error">
            <p data-bind="text: message"></p>
        </div>
    </div>
</div>

