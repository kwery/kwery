package com.kwery.tests.services.job.email.reporteamailcreator.sqlqueryemailsetting;

import org.junit.Test;

import java.util.LinkedList;

public class SqlQueryEmailSettingNotPresentTest extends AbstractSqlQueryEmailSettingTest {
    @Test
    public void test() throws Exception {
        assertEmail(false, false, reportEmailCreator.create(jobExecutionModel, new LinkedList<>()));
    }
}
