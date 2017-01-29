package com.kwery.tests.fluentlenium;

import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;

import java.util.List;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class KweryFluentPage extends FluentPage {
    public void waitForModalDisappearance() {
        await().pollingEvery(1, MILLISECONDS).atMost(TIMEOUT_SECONDS, SECONDS).until($(".modal-backdrop")).not().present();
    }

    public void waitForModalAppearance() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".waiting-modal-f")).displayed();
    }

    public void waitForSuccessMessage(String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-success-message p")).text(message);
    }

    public void waitForFailureMessage(String message) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-failure-message p")).text(message);
    }

    public void waitForFailureMessageDisplay() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".f-failure-message")).displayed();
    }

    public List<String> getErrorMessages() {
        return $(".f-failure-message p").stream().map(FluentWebElement::text).collect(toList());
    }

}
