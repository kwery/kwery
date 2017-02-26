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
    public void test() throws Exception {
        page.waitForModalDisappearance();
        page.search(searchString);
        page.waitForModalDisappearance();

        page1();

        page.getPaginationComponent(getPaginationPosition()).clickNext();
        page.waitForModalDisappearance();

        page.search("");
        page.waitForModalDisappearance();
        page.assertStartingPage();
    }

    private void page1() throws Exception {
        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(0)));
        page.getPaginationComponent(getPaginationPosition()).assertNextState(true);
        page.getPaginationComponent(getPaginationPosition()).assertPreviousState(false);
    }
}
