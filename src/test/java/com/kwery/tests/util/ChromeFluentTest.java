package com.kwery.tests.util;

import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.concurrent.TimeUnit;

public class ChromeFluentTest extends FluentTest {
    @Rule
    public Timeout globalTimeout = new Timeout(new Long(TimeUnit.MINUTES.toMillis(3)).intValue());

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
