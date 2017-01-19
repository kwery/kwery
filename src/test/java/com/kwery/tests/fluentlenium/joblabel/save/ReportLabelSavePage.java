package com.kwery.tests.fluentlenium.joblabel.save;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.REPORT_LABEL_SAVE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class ReportLabelSavePage extends KweryFluentPage implements RepoDashPage {
    public static final String INPUT_VALIDATION_ERROR_MESSAGE = "Please fill in this field.";
    public static final String SELECT_VALIDATION_ERROR_MESSAGE = "Please select an item in the list.";

    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".label-form-f").isDisplayed();
        return true;
    }

    @Override
    public String getUrl() {
        return "/#report-label/add";
    }

    public void fillForm(String label, Integer parentLabelIndex) {
        fillName(label);
        if (parentLabelIndex != null) {
            optParentLabel();
            parentLabel(parentLabelIndex);
        }
    }

    public void fillAndSubmitForm(String label, Integer parentLabelIndex) {
        fillForm(label, parentLabelIndex);
        submitForm();
    }

    public void parentLabel(Integer parentLabelIndex) {
        findFirst(className("parent-label-f")).fillSelect().withIndex(parentLabelIndex);
    }

    public void fillName(String label) {
        fill(".label-name-f").with(label);
    }

    public void optParentLabel() {
        find(className("parent-label-opted-f")).click();
    }

    public void submitForm() {
        waitForModalDisappearance();
        find(className("submit-f")).click();
    }

    public void waitForLabelSaveSuccessMessage(String label) {
        super.waitForSuccessMessage(MessageFormat.format(REPORT_LABEL_SAVE_SUCCESS_M, label));
    }

    public void waitForLabelNameValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".name-error-f").hasText(INPUT_VALIDATION_ERROR_MESSAGE);
    }

    public void waitForLabelNameValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".name-error-f").hasText("");
    }

    public void waitForParentLabelValidationError() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".parent-error-f").hasText(SELECT_VALIDATION_ERROR_MESSAGE);
    }

    public void waitForParentLabelValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".parent-error-f").hasText("");
    }

    public String parentLabelText(int index) {
        return $(className(String.format("parent-label-%d-f", index))).getText();
    }

    public List<String> parentLabelTexts() {
        List<String> labels = new LinkedList<>();

        for (FluentWebElement option : $(".parent-label-f option")) {
            labels.add(option.getText());
        }

        return labels;
    }

    public void waitForJobLabelListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> url().equals("/#report-label/list"));
    }
}
