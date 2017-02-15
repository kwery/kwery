package com.kwery.tests.fluentlenium.job.reportlist;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.components.ComponentInstantiator;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withClass;

@FindBy(css = ".pagination-f")
public class PaginationComponent extends FluentWebElement {
    public PaginationComponent(WebElement element, FluentControl control, ComponentInstantiator instantiator) {
        super(element, control, instantiator);
    }

    public void assertPreviousState(boolean enabled) {
        if (enabled) {
            assertThat($(".previous-f", withClass().notContains("disabled"))).hasSize(1);
        } else {
            assertThat($(".previous-f", withClass().contains("disabled"))).hasSize(1);
        }
    }

    public void assertNextState(boolean enabled) {
        if (enabled) {
            assertThat($(".next-f", withClass().notContains("disabled"))).hasSize(1);
        } else {
            assertThat($(".next-f", withClass().contains("disabled"))).hasSize(1);
        }
    }

    public void clickPrevious() {
        $(By.id("previous")).click();
    }

    public void clickNext() {
        $(By.id("next")).click();
    }
}
