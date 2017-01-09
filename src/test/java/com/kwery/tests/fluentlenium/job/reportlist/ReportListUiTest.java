package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.tests.fluentlenium.job.reportlist.AbstractReportListUiTest;
import com.kwery.tests.fluentlenium.job.reportlist.ReportListRow;
import org.junit.Test;

import java.util.List;

import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class ReportListUiTest extends AbstractReportListUiTest {
    @Test
    public void test() {
        page.waitForRows(3);
        List<ReportListRow> rows = page.rows();
        for (ReportListRow row : rows) {
            assertThat(row, theSameBeanAs(rowMap.get(row.getLabel())));
        }
    }
}
