package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import org.junit.Test;

public class SqlQueryEmailSettingTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void testAllFalse() throws Exception {
        setEmailSetting(false, false);
        reportEmailSender.send(jobExecutionModel);
        assertEmail(true, true);
    }

    @Test
    public void testAllTrue() throws Exception {
        setEmailSetting(true, true);
        reportEmailSender.send(jobExecutionModel);
        assertEmail(false, false);
    }

    @Test
    public void testTrueFalse() throws Exception {
        setEmailSetting(true, false);
        reportEmailSender.send(jobExecutionModel);
        assertEmail(false, true);
    }

    @Test
    public void testFalseTrue() throws Exception {
        setEmailSetting(false, true);
        reportEmailSender.send(jobExecutionModel);
        assertEmail(true, false);
    }
}
