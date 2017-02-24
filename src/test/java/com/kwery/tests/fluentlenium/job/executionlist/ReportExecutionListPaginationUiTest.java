package com.kwery.tests.fluentlenium.job.executionlist;

import com.kwery.tests.fluentlenium.job.executionlist.ReportExecutionListPage.ReportExecution;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportExecutionListPaginationUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(2);
        super.setUp();
    }

    @Test
    public void test() {
        page.assertRows(2);
        assertThat(page.isPreviousEnabled(), is(false));

        for (int i = 0; i < 2; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i, reportExecutionMap);
        }

        page.clickNext();
        page.waitUntilPreviousIsEnabled();

        for (int i = 2; i < 4; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i - 2, reportExecutionMap);
        }

        assertThat(page.isNextEnabled(), is(false));

        page.clickPrevious();
        page.waitUntilPreviousIsDisabled();

        page.assertRows(2);

        for (int i = 0; i < 2; ++i) {
            Map<ReportExecution, ?> reportExecutionMap = toMap(controller.jobExecutionModelToJobExecutionDto(models.get(i)), jobModel);
            page.assertReportExecutionList(i, reportExecutionMap);
        }

        assertThat(page.isNextEnabled(), is(true));
    }
}
