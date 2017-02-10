package com.kwery.tests.services.job.email.sqlqueryemailsetting;

import org.junit.Test;

public class SqlQueryEmailSettingNotPresentTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void test() throws Exception {
        reportEmailSender.send(jobExecutionModel);
        assertEmail(false, false);
    }
}
