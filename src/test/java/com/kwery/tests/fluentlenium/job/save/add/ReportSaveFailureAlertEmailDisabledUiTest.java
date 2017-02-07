package com.kwery.tests.fluentlenium.job.save.add;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportSaveFailureAlertEmailDisabledUiTest extends AbstractReportSaveUiTest {
    @Before
    public void setUp() {
        this.setUrlConfigurationSave(false);
        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.isFailureAlertEmailFieldEnabled(), is(false));
    }
}
