package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

public class ReportExecutionListDeleteReportExecutionUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(4);
        super.setUp();
    }

    @Test
    public void test() {
        page.waitForRows(4);
        page.deleteExecution(0);
        page.waitForDeleteSuccessMessage();
        page.waitForRowDelete(0);
    }
}
