package com.kwery.tests.fluentlenium;

import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;

public class KweryFluentPage extends FluentPage {
    public void waitForModalDisappearance() {
        waitForElementDisappearance(className("modal-backdrop"));
    }

    public void waitForElementDisappearance(By by) {
        waitForElementDisappearance(by, 1, SECONDS);
    }

    public void waitForElementDisappearance(By by, long value, TimeUnit unit) {
        try {
            getDriver().manage().timeouts().implicitlyWait(value, unit);
            long start = System.currentTimeMillis();
            do {
                try {
                    getDriver().findElement(by);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        //Ignore
                    }
                } catch (NoSuchElementException e) {
                    return;
                }
            } while (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) < TIMEOUT_SECONDS);
        } finally {
            getDriver().manage().timeouts().implicitlyWait(TestUtil.TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        throw new RuntimeException();
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
