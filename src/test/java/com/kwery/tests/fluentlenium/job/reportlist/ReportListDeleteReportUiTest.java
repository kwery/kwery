package com.kwery.tests.fluentlenium.job.reportlist;

import com.kwery.models.JobModel;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ReportListDeleteReportUiTest extends AbstractReportListUiTest {
    @Before
    public void setUp() {
        this.setResultCount(3);
        super.setUp();
    }

    @Test
    public void test() throws Exception {
        page.waitForModalDisappearance();
        page.deleteReport(getRow(jobModel));
        page.waitForModalDisappearance();
        page.assertDeleteSuccessMessage();

        List<JobModel> modified = removeJobModel(jobModel);
        page.assertReportList(modified.size());

        for (int i = 0; i < modified.size(); ++i) {
            page.assertReportListRow(i, toReportRowMap(modified.get(i)));
        }
    }

    @Test
    public void testDeleteParentReport() throws Exception {
        page.waitForModalDisappearance();
        page.deleteReport(getRow(parentJob));
        page.waitForModalDisappearance();
        page.assertDeleteFailureMessage();
        page.assertReportList(jobModels.size());

        for (int i = 0; i < jobModels.size(); ++i) {
            page.assertReportListRow(i, toReportRowMap(jobModels.get(i)));
        }
    }

    private int getRow(JobModel jobModel) {
        for (int i = 0; i < jobModels.size(); ++i) {
            if (jobModel.getName().equals(jobModels.get(i).getName())) {
                return i;
            }
        }

        return -1;
    }
}
