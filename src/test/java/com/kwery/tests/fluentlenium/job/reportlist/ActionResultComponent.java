package com.kwery.tests.fluentlenium.job.reportlist;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.filter.FilterConstructor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;

@FindBy(css = ".action-result-f")
public class ActionResultComponent extends FluentWebElement {
    public ActionResultComponent(WebElement element, FluentControl control, ComponentInstantiator instantiator) {
        super(element, control, instantiator);
    }

    public void assertSuccessMessage(String message) {
        assertThat(el(".f-success-message p", FilterConstructor.withText(message))).isDisplayed();
    }

    public void assertFailureMessage(String message) {
        assertThat(el(".f-failure-message p", FilterConstructor.withText(message))).isDisplayed();
    }
}
