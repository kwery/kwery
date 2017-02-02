package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.models.JobExecutionModel.Status.*;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportExecutionListFilterWithPaginationUiTest extends AbstractReportExecutionListUiTest {
    @Before
    public void setUp() throws Exception {
        this.setResultCount(1);
        super.setUp();
        page.waitForModalDisappearance();
    }

    @Test
    public void test() {
        String start = "Sat Jan 07 2017 05:05";
        String end = "Sat Jan 07 2017 05:40";
        page.filterResult(start, end);
        page.waitForModalDisappearance();
        page.waitForExecutionListTableUpdate("Sat Jan 07 2017 05:30");
        List<ReportExecutionListRow> row = page.executionListTable();
        assertThat(row, hasSize(1));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:30", "Sat Jan 07 2017 05:40", FAILURE.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem1.getExecutionId()))));

        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));

        page.clickNext();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsEnabled();
        row = page.executionListTable();
        assertThat(row, hasSize(1));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:10", "Sat Jan 07 2017 05:20", SUCCESS.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem0.getExecutionId()))));

        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(false));

        page.clickPrevious();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsDisabled();
        row = page.executionListTable();
        assertThat(row, hasSize(1));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 05:30", "Sat Jan 07 2017 05:40", FAILURE.name(),
                ninjaServerRule.getServerUrl() + String.format("/#report/%d/execution/%s", jobModel.getId(), jem1.getExecutionId()))));

        assertThat(page.isNextEnabled(), is(true));

        //Clicking on filter in between pages takes you back to the first page
        page.clickNext();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsEnabled();
        start = "Sat Jan 07 2017 06:05";
        end = "Sat Jan 07 2017 06:15";
        page.filterResult(start, end);
        page.waitForModalDisappearance();
        page.waitForExecutionListTableUpdate("Sat Jan 07 2017 06:10");
        row = page.executionListTable();
        assertThat(row, hasSize(1));
        assertThat(row.get(0), theSameBeanAs(new ReportExecutionListRow("Sat Jan 07 2017 06:10", "", ONGOING.name(), null)));
        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(false)); //Only one result in the range
    }
}
