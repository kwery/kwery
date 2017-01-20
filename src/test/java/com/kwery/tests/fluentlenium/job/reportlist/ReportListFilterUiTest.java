package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ReportListFilterUiTest extends AbstractReportListUiTest {
    @Test
    public void test() throws Exception {
        page.waitForRows(3);
        //TODO - fix this, sleep is a hacky way to wait for knockout to finish populating the page
        SECONDS.sleep(10);
        List<ReportListRow> rows = page.rows();
        for (ReportListRow row : rows) {
            ReportListRow expected = rowMap.get(row.getLabel());
            assertThat(row, theSameBeanAs(expected));
        }

        assertThat(page.labelTexts(), containsInAnyOrder(jobLabelModel.getLabel(), ""));

        page.selectLabel(0);
        page.filterReport();
        page.waitForModalDisappearance();
        page.waitForRows(3);
        //TODO - fix this, sleep is a hacky way to wait for knockout to finish populating the page
        SECONDS.sleep(10);

        rows = page.rows();
        for (ReportListRow row : rows) {
            ReportListRow expected = rowMap.get(row.getLabel());
            assertThat(row, theSameBeanAs(expected));
        }

        page.selectLabel(1);
        page.filterReport();
        page.waitForModalDisappearance();
        page.waitForRows(1);
        //TODO - fix this, sleep is a hacky way to wait for knockout to finish populating the page
        SECONDS.sleep(10);

        rows = page.rows();
        for (ReportListRow row : rows) {
            ReportListRow expected = rowMap.get(row.getLabel());
            assertThat(row, theSameBeanAs(expected));
        }
    }
}
