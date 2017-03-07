package com.kwery.tests.fluentlenium.job.save.add;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReportSaveFailureAlertEmailDisabledUiTest extends AbstractReportSaveUiTest {
    protected boolean onboardingFlow;

    public ReportSaveFailureAlertEmailDisabledUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameterized.Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    @Before
    public void setUp() {
        super.setOnboardingFlow(onboardingFlow);
        this.setUrlConfigurationSave(false);
        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.isFailureAlertEmailFieldEnabled(), is(false));
    }
}
