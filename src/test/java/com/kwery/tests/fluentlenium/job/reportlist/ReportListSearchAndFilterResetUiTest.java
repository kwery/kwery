package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.models.JobModel;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ReportListSearchAndFilterResetUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        setResultCount(1);
        super.setUp();
    }

    @Test
    public void testFilterReset() {
        page.waitForModalDisappearance();
        page.search(searchString);
        page.waitForModalDisappearance();

        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(0)));
        page.getPaginationComponent().clickNext();
        page.waitForModalDisappearance();

        page.assertReportListRow(0, toReportRowMap(expectedSearchOrder.get(1)));

        page.selectLabel(1);

        page.waitForModalDisappearance();
        page.assertStartingPage();
    }

    @Test
    public void testSearchReset() {
        page.waitForModalDisappearance();
        page.selectLabel(1);
        page.waitForModalDisappearance();

        List<JobModel> jobs = removeJobModel(childJob);

        page.assertReportListRow(0, toReportRowMap(jobs.get(0)));
        page.getPaginationComponent().clickNext();
        page.waitForModalDisappearance();

        page.assertReportListRow(0, toReportRowMap(jobs.get(1)));

        page.search(searchString);

        page.waitForModalDisappearance();
        page.assertStartingPage();
    }
}
