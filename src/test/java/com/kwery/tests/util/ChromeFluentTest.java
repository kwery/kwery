package com.kwery.tests.util;

import org.fluentlenium.adapter.junit.FluentTest;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.className;

public class ChromeFluentTest extends FluentTest {
    @Before
    public void setUpChromFluentTest() {
        TestUtil.clearCreatedIds();
    }

    @Rule
    public Timeout globalTimeout = new Timeout(new Long(TimeUnit.MINUTES.toMillis(10)).intValue());

    @Override
    public Long getImplicitlyWait() {
        return TimeUnit.MINUTES.toMillis(1);
    }

    @Override
    public TriggerMode getScreenshotMode() {
        return TriggerMode.AUTOMATIC_ON_FAIL;
    }

    @Override
    public String getScreenshotPath() {
        return "/tmp";
    }

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
