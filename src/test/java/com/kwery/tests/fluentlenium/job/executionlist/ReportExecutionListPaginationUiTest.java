package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.models.JobExecutionModel.Status.*;
import static com.kwery.models.SqlQueryExecutionModel.Status.FAILURE;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
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
        page.waitForRows(2);
        assertThat(page.isPreviousEnabled(), is(false));
        List<ReportExecutionListRow> row = page.executionListTable();
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 06:10", "", ONGOING.name(), null)));
        assertThat(row.get(1), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:50", "Sat Jan 07 2017 06:00", KILLED.name(), null)));

        page.clickNext();
        page.waitUntilPreviousIsEnabled();
        row = page.executionListTable();
        assertThat(row, hasSize(2));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:30", "Sat Jan 07 2017 05:40", FAILURE.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem1.getExecutionId()))));
        assertThat(row.get(1), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:10", "Sat Jan 07 2017 05:20", SUCCESS.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem0.getExecutionId()))));
        assertThat(page.isNextEnabled(), is(false));

        page.clickPrevious();
        page.waitUntilPreviousIsDisabled();
        row = page.executionListTable();
        assertThat(row, hasSize(2));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 06:10", "", ONGOING.name(), null)));
        assertThat(row.get(1), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:50", "Sat Jan 07 2017 06:00", KILLED.name(), null)));
        assertThat(page.isNextEnabled(), is(true));
    }
}
