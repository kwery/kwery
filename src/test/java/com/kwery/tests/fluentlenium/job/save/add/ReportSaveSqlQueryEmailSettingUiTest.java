package com.kwery.tests.fluentlenium.job.save.add;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReportSaveSqlQueryEmailSettingUiTest extends AbstractReportSaveUiTest {
    protected boolean onboardingFlow;

    public ReportSaveSqlQueryEmailSettingUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true},
                {false},
        });
    }

    @Before
    public void setUp() {
        setNoEmailSetting(true);
        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.sqlQueryEmailSettingIncludeAsEmailAttachment(0), is(true));
        assertThat(page.sqlQueryEmailSettingIncludeInEmailBody(0), is(true));
        assertThat(page.sqlQueryEmailSettingIgnoreLabel(0), is(false));

        page.clickOnAddSqlQuery(0);

        assertThat(page.sqlQueryEmailSettingIncludeAsEmailAttachment(1), is(true));
        assertThat(page.sqlQueryEmailSettingIncludeInEmailBody(1), is(true));
        assertThat(page.sqlQueryEmailSettingIgnoreLabel(1), is(false));
    }
}
