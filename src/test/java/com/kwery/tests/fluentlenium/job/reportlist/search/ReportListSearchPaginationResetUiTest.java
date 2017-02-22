package com.kwery.tests.fluentlenium.job.reportlist.search;

import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import org.junit.Before;
import org.junit.Test;

public class ReportListSearchPaginationResetUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        setResultCount(1);
        super.setUp();
    }

    @Test
    public void test() {
        page.waitForModalDisappearance();
        page.search(searchString);
        page.waitForModalDisappearance();

        page1();

        page.getPaginationComponent().clickNext();
        page.waitForModalDisappearance();

        page.search("");
        page.waitForModalDisappearance();
        page.assertStartingPage();
    }

    private void page1() {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(0)));
        page.getPaginationComponent().assertNextState(true);
        page.getPaginationComponent().assertPreviousState(false);
    }
}
