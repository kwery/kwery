<div class="row">
    <div class="twelve columns">
        <div id="actionResultDialog" data-bind="dialog: {isOpen: isOpen, modal: true, closeOnEscape: false, dialogClass: 'action-result-dialog'}">
            <p data-bind="text: message"></p>
            <a data-bind="text: nextActionName, attr: {href: nextAction}" id="nextAction" class="button button-primary"></a>
        </div>

        <div data-bind="visible: showSuccess" class="isa_info">
            <p data-bind="text: message"></p>
        </div>

        <div data-bind="visible: showFailure" class="isa_error">
            <p data-bind="text: message"></p>
        </div>
    </div>
</div>

