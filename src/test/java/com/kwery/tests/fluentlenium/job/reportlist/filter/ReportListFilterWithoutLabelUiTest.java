package com.kwery.tests.fluentlenium.job.reportlist.filter;

import com.google.common.collect.ImmutableList;
import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import org.junit.Before;
import org.junit.Test;

public class ReportListFilterWithoutLabelUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        setResultCount(2);
        super.setUp();
    }

    @Test
    public void test() {
        page.assertLabelTexts(ImmutableList.of(jobLabelModel.getLabel()));

        page.selectLabel(1);
        page.waitForModalDisappearance();
        page.selectLabel(0);
        page.waitForModalDisappearance();

        page.assertReportList(2);
        page.assertReportListRow(0, toReportRowMap(jobModels.get(0)));
        page.assertReportListRow(1, toReportRowMap(jobModels.get(1)));

        page.getPaginationComponent(getPaginationPosition()).clickNext();

        page.waitForModalDisappearance();
        page.assertReportListRow(0, toReportRowMap(jobModels.get(2)));

        page.getPaginationComponent(getPaginationPosition()).clickPrevious();
        page.waitForModalDisappearance();

        page.assertReportListRow(0, toReportRowMap(jobModels.get(0)));
        page.assertReportListRow(1, toReportRowMap(jobModels.get(1)));
    }
}
