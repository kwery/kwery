package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportListUiTest extends AbstractReportListUiTest {
    @Test
    public void test() throws InterruptedException {
        page.waitForRows(3);
        //TODO - fix this, sleep is a hacky way to wait for knockout to finish populating the page
        TimeUnit.SECONDS.sleep(10);
        List<ReportListRow> rows = page.rows();
        for (ReportListRow row : rows) {
            ReportListRow expected = rowMap.get(row.getLabel());
            assertThat(row, theSameBeanAs(expected));
        }
    }
}
