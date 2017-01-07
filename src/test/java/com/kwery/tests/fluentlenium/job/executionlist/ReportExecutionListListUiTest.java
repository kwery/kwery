package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.JobExecutionModel.Status.*;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportExecutionListListUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(4);
        super.setUp();
    }

    @Test
    public void test() {
        page.waitForRows(4);
        List<ReportExecutionListRow> row = page.executionListTable();
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 06:10", "", ONGOING.name(), null)));
        assertThat(row.get(1), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:50", "Sat Jan 07 2017 06:00", KILLED.name(), null)));
        assertThat(row.get(2), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:30", "Sat Jan 07 2017 05:40", FAILURE.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem1.getExecutionId()))));
        assertThat(row.get(3), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:10", "Sat Jan 07 2017 05:20", SUCCESS.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem0.getExecutionId()))));
    }
}
