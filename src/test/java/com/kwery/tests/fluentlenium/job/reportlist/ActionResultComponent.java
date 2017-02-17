package com.kwery.tests.fluentlenium.job.reportlist;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

@FindBy(css = ".action-result-f")
public class ActionResultComponent extends FluentWebElement {
    public ActionResultComponent(WebElement element, FluentControl control, ComponentInstantiator instantiator) {
        super(element, control, instantiator);
    }

    public void assertSuccessMessage(String message) {
        assertThat(el(".f-success-message p", withText(message))).isDisplayed();
    }

    public void assertFailureMessage(String message) {
        assertThat(el(".f-failure-message p", withText().contains(message))).isDisplayed();
    }

    public void assertFailureMessages(List<String> messages) {
        for (String message : messages) {
            assertThat(el(".f-failure-message p", withText().contains(message))).isDisplayed();
        }
    }
}
