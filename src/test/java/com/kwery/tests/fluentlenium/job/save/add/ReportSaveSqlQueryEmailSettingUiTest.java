package com.kwery.tests.fluentlenium.job.save.add;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportSaveSqlQueryEmailSettingUiTest extends AbstractReportSaveUiTest {
    @Before
    public void setUp() {
        setNoEmailSetting(true);
        super.setUp();
    }

    @Test
    public void test() {
        assertThat(page.sqlQueryEmailSettingIncludeAsEmailAttachment(0), is(true));
        assertThat(page.sqlQueryEmailSettingIncludeInEmailBody(0), is(true));

        page.clickOnAddSqlQuery(0);

        assertThat(page.sqlQueryEmailSettingIncludeAsEmailAttachment(1), is(true));
        assertThat(page.sqlQueryEmailSettingIncludeInEmailBody(1), is(true));
    }
}
