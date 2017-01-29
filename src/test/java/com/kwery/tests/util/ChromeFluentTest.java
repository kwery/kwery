package com.kwery.tests.util;

import org.fluentlenium.adapter.junit.FluentTest;

import java.util.concurrent.TimeUnit;

public class ChromeFluentTest extends FluentTest {
@Override
    public Long getImplicitlyWait() {
        return TimeUnit.MINUTES.toMillis(1);
    }
}
