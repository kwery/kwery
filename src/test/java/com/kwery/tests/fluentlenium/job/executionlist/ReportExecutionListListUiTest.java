package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ReportExecutionListListUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(4);
        super.setUp();
    }

    @Test
    public void test() {
        for (int i = 0; i < models.size(); ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i, reportExecutionMap);
        }
    }
}
