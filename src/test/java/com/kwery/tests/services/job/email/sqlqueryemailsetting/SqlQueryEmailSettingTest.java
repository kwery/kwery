package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import org.junit.Test;

import java.util.LinkedList;

public class SqlQueryEmailSettingTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void testAllFalse() throws Exception {
        setEmailSetting(false, false);
        reportEmailSender.send(jobExecutionModel, new LinkedList<>());
        assertEmail(true, true);
    }

    @Test
    public void testAllTrue() throws Exception {
        setEmailSetting(true, true);
        reportEmailSender.send(jobExecutionModel, new LinkedList<>());
        assertEmail(false, false);
    }

    @Test
    public void testTrueFalse() throws Exception {
        setEmailSetting(true, false);
        reportEmailSender.send(jobExecutionModel, new LinkedList<>());
        assertEmail(false, true);
    }

    @Test
    public void testFalseTrue() throws Exception {
        setEmailSetting(false, true);
        reportEmailSender.send(jobExecutionModel, new LinkedList<>());
        assertEmail(true, false);
    }
}
