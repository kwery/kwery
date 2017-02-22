package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Before;
import org.junit.Test;

public class ReportListUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {

        super.setResultCount(1);
        super.setUp();
        page.waitForModalDisappearance();
    }

    @Test
    public void test() throws InterruptedException {
        page1();
        page.getPaginationComponent(getPaginationPosition()).clickNext();

        page2();
        page.getPaginationComponent(getPaginationPosition()).clickNext();

        page3();
        page.getPaginationComponent(getPaginationPosition()).clickPrevious();

        page2();
        page.getPaginationComponent(getPaginationPosition()).clickPrevious();

        page1();
    }

    private void page3() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(2)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(false);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);
    }

    private void page2() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(1)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);
    }

    private void page1() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(0)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
    }
}
