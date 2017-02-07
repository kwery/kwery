package com.kwery.tests.fluentlenium.job.reportlist;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportListDeleteReportUiTest extends AbstractReportListUiTest {
    @Test
    public void test() {
        List<ReportListRow> rows = page.rows();
        for (int j = 0; j < rows.size(); ++j) {
            ReportListRow row = rows.get(j);
            if (!parentJobName.equals(row.getLabel())) {
                page.deleteReport(j);
                page.waitForModalDisappearance();
                page.waitForRows(2);
                page.waitForDeleteSuccessMessage();
                break;
            }
        }

        rows = page.rows();
        for (int j = 0; j < rows.size(); ++j) {
            ReportListRow row = rows.get(j);
            if (!parentJobName.equals(row.getLabel())) {
                page.deleteReport(j);
                page.waitForModalDisappearance();
                page.waitForRows(1);
                page.waitForDeleteSuccessMessage();
                break;
            }
        }

        assertThat(page.rows(), hasSize(1));
        assertThat(page.rows().get(0).getLabel(), is(parentJobName));
    }

    @Test
    public void testDeleteParentReport() {
        List<ReportListRow> rows = page.rows();
        for (int j = 0; j < rows.size(); ++j) {
            ReportListRow row = rows.get(j);
            if (parentJobName.equals(row.getLabel())) {
                page.deleteReport(j);
                page.waitForDeleteFailureMessage();
                break;
            }
        }

        assertThat(page.rows(), hasSize(3));
    }
}
