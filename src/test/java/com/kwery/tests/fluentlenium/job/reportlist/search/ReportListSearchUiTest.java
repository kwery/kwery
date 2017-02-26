package com.kwery.tests.fluentlenium.job.reportlist.search;

import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import org.junit.Before;
import org.junit.Test;

public class ReportListSearchUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        super.setResultCount(1);
        super.setUp();
        page.waitForModalDisappearance();
    }

    @Test
    public void test() throws Exception {
        page.search(searchString);
        page.waitForModalDisappearance();

        page1();
        page.getPaginationComponent(getPaginationPosition()).clickNext();
        page.waitForModalDisappearance();

        page2();
        page.getPaginationComponent(getPaginationPosition()).clickNext();
        page.waitForModalDisappearance();

        page3();
        page.getPaginationComponent(getPaginationPosition()).clickPrevious();
        page.waitForModalDisappearance();

        page2();
        page.getPaginationComponent(getPaginationPosition()).clickPrevious();
        page.waitForModalDisappearance();

        page1();
    }

    private void page3() throws Exception {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(2)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(false);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);
    }

    private void page2() throws Exception {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(1)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(true);
    }

    private void page1() throws Exception {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(0)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
    }
}
