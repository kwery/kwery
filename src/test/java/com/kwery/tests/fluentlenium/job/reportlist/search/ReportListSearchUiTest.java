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
    public void test() {
        page.search(searchString);
        page.waitForModalDisappearance();

        page1();
        page.getPaginationComponent().clickNext();
        page.waitForModalDisappearance();

        page2();
        page.getPaginationComponent().clickNext();
        page.waitForModalDisappearance();

        page3();
        page.getPaginationComponent().clickPrevious();
        page.waitForModalDisappearance();

        page2();
        page.getPaginationComponent().clickPrevious();
        page.waitForModalDisappearance();

        page1();
    }

    private void page3() {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(2)));
        page.getPaginationComponent().assertNextState(false);
        page.getPaginationComponent().assertPreviousState(true);
    }

    private void page2() {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(1)));
        page.getPaginationComponent().assertNextState(true);
        page.getPaginationComponent().assertPreviousState(true);
    }

    private void page1() {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(0)));
        page.getPaginationComponent().assertNextState(true);
        page.getPaginationComponent().assertPreviousState(false);
    }
}
