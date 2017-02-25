package com.kwery.tests.fluentlenium.joblabel.save;

import com.kwery.tests.fluentlenium.KweryFluentPage;
import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.tests.util.Messages.REPORT_LABEL_SAVE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;
import static org.fluentlenium.core.filter.FilterConstructor.withTextContent;
import static org.openqa.selenium.By.className;

@Wait(timeUnit = SECONDS, timeout = TIMEOUT_SECONDS)
@PageUrl("/#report-label/add")
public class ReportLabelSavePage extends KweryFluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".label-form-f")).displayed();
        return true;
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
        el(className("parent-label-f")).fillSelect().withIndex(parentLabelIndex);
    }

    public void fillName(String label) {
        $(".label-name-f").fill().with(label);
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

    public void assertNonEmptyLabelNameValidationError() {
        assertThat(el("div", withClass().contains("name-error-f"), withTextContent().notContains("")));
    }

    public void waitForLabelNameValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".name-error-f")).text("");
    }

    public void assertNonEmptyParentLabelValidationError() {
        assertThat(el("div", withClass().contains("parent-error-f"), withTextContent().notContains("")));
    }

    public void waitForParentLabelValidationErrorRemoval() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".parent-error-f")).text("");
    }

    public String parentLabelText(int index) {
        return $(className(String.format("parent-label-%d-f", index))).text();
    }

    public List<String> parentLabelTexts() {
        List<String> labels = new LinkedList<>();

        for (FluentWebElement option : $(".parent-label-f option")) {
            labels.add(option.text());
        }

        return labels;
    }

    public void waitForJobLabelListPage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> getDriver().getCurrentUrl().equals(getBaseUrl() + "/#report-label/list"));
    }
}
