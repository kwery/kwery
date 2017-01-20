package com.kwery.tests.fluentlenium;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.List;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class KweryFluentPage extends FluentPage {
    public void waitForModalDisappearance() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".waiting-modal-f").isNotDisplayed();
    }

    public void waitForModalAppearance() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".waiting-modal-f").isDisplayed();
    }

    public void waitForSuccessMessage(String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message p").hasText(message);
    }

    public void waitForFailureMessage(String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message p").hasText(message);
    }

    public void waitForFailureMessageDisplay() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-failure-message").isDisplayed();
    }

    public List<String> getErrorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::getText).collect(toList());
    }

}
