package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ReportExecutionListPaginationUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(2);
        super.setUp();
    }

    @Test
    public void test() {
        page.assertRows(2);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);

        for (int i = 0; i < 2; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i, reportExecutionMap);
        }

        page.getPaginationComponent(getPaginationPosition()).clickNext();
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);

        for (int i = 2; i < 4; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i - 2, reportExecutionMap);
        }

        page.getPaginationComponent(getPaginationPosition()).assertNextState(false);
        page.getPaginationComponent(getPaginationPosition()).clickPrevious();
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);

        page.assertRows(2);

        for (int i = 0; i < 2; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i, reportExecutionMap);
        }

        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
    }
}
