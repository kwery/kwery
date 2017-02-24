package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ReportExecutionListDeleteReportExecutionUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(4);
        super.setUp();
    }

    @Test
    public void test() {
        page.deleteExecution(0);
        page.assertDeleteSuccessMessage();

        for (int i = 1; i < models.size(); ++i) {
            Map<ReportExecutionListPage.ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i - 1, reportExecutionMap);
        }
    }
}
