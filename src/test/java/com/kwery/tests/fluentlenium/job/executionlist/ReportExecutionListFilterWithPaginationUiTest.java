package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

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

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem1), jobModel));
        page.assertRows(1);

        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(true));

        page.clickNext();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsEnabled();

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem0), jobModel));
        page.assertRows(1);

        assertThat(page.isPreviousEnabled(), is(true));
        assertThat(page.isNextEnabled(), is(false));

        page.clickPrevious();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsDisabled();

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem1), jobModel));
        page.assertRows(1);

        assertThat(page.isNextEnabled(), is(true));

        //Clicking on filter in between pages takes you back to the first page
        page.clickNext();
        page.waitForModalDisappearance();
        page.waitUntilPreviousIsEnabled();

        start = "Sat Jan 07 2017 06:05";
        end = "Sat Jan 07 2017 06:15";
        page.filterResult(start, end);
        page.waitForModalDisappearance();

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem3), jobModel));
        page.assertRows(1);

        assertThat(page.isPreviousEnabled(), is(false));
        assertThat(page.isNextEnabled(), is(false)); //Only one result in the range
    }
}
