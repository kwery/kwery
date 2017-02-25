package com.kwery.tests.util;

import org.fluentlenium.adapter.junit.FluentTest;

import java.util.concurrent.TimeUnit;

public class ChromeFluentTest extends FluentTest {
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
}
