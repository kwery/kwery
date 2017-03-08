package com.kwery.tests.fluentlenium.job.reportlist.order;

import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.job.save.update.ReportUpdateSuccessUiTest;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ReportListPostUpdateOrderUiTest extends ReportUpdateSuccessUiTest {
    @Before
    public void setUp() {
        this.setSkipDaoCheck(true);
        super.setUp();
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);
    }

    @Test
    public void test() {
        super.test();
        page.waitForModalDisappearance();
        assertThat(el(String.format(".report-list-%d-f .title-f", 0), withText(jobDao.getJobById(jobModel.getId()).getTitle()))).isDisplayed();
    }
}
