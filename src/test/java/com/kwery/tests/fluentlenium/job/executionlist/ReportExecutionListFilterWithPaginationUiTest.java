package com.kwery.tests.fluentlenium.job.executionlist;

import org.junit.Before;
import org.junit.Test;

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

        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);

        page.getPaginationComponent(getPaginationPosition()).clickNext();

        page.waitForModalDisappearance();

        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem0), jobModel));
        page.assertRows(1);

        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);
        page.getPaginationComponent(getPaginationPosition()).assertNextState(false);

        page.getPaginationComponent(getPaginationPosition()).clickPrevious();
        page.waitForModalDisappearance();
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem1), jobModel));
        page.assertRows(1);

        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);

        //Clicking on filter in between pages takes you back to the first page
        page.getPaginationComponent(getPaginationPosition()).clickNext();
        page.waitForModalDisappearance();
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);

        start = "Sat Jan 07 2017 06:05";
        end = "Sat Jan 07 2017 06:15";
        page.filterResult(start, end);
        page.waitForModalDisappearance();

        page.assertReportExecutionList(0, toMap(controller.jobExecutionModelToJobExecutionDto(jem3), jobModel));
        page.assertRows(1);

        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
    }
}
