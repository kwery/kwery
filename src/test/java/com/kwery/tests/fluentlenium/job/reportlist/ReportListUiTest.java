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
        page.getPaginationComponent().clickNext();

        page2();
        page.getPaginationComponent().clickNext();

        page3();
        page.getPaginationComponent().clickPrevious();

        page2();
        page.getPaginationComponent().clickPrevious();

        page1();
    }

    private void page3() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(2)));
        page.getPaginationComponent().assertNextState(false);
        page.getPaginationComponent().assertPreviousState(true);
    }

    private void page2() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(1)));
        page.getPaginationComponent().assertNextState(true);
        page.getPaginationComponent().assertPreviousState(true);
    }

    private void page1() {
        page.assertReportListRow(0, toReportRowMap(jobModels.get(0)));
        page.getPaginationComponent().assertNextState(true);
        page.getPaginationComponent().assertPreviousState(false);
    }
}
