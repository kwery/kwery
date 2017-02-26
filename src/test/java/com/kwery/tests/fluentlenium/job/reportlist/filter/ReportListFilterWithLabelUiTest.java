package com.kwery.tests.fluentlenium.job.reportlist.filter;

import com.google.common.collect.ImmutableList;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ReportListFilterWithLabelUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        super.setResultCount(1);
        super.setUp();
    }

    @Test
    public void test() throws Exception {
        //page 0
        page.assertLabelTexts(ImmutableList.of(jobLabelModel.getLabel()));
        page.selectLabel(1);
        page.waitForModalDisappearance();

        page.assertReportList(1);

        List<JobModel> jobs = removeJobModel(childJob);

        page.assertReportListRow(0, toReportRowMap(jobs.get(0)));

        page.getPaginationComponent(getPaginationPosition()).clickNext();

        //page 1
        page.waitForModalDisappearance();
        page.assertReportList(1);
        page.assertReportListRow(0, toReportRowMap(jobs.get(1)));

        page.getPaginationComponent(getPaginationPosition()).clickPrevious();

        page.waitForModalDisappearance();

        //page 0
        page.assertReportList(1);
        page.assertReportListRow(0, toReportRowMap(jobs.get(0)));
    }
}
