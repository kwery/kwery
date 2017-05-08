package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import org.junit.Test;

import java.util.LinkedList;

public class SqlQueryEmailSettingNotPresentTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void test() throws Exception {
        reportEmailSender.send(jobExecutionModel, new LinkedList<>());
        assertEmail(false, false);
    }
}
