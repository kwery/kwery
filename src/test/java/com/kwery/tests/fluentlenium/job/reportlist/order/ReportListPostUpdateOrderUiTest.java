package com.kwery.tests.fluentlenium.job.reportlist.order;

import com.kwery.tests.fluentlenium.job.save.update.AbstractReportUpdateSuccessUiTest;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ReportListPostUpdateOrderUiTest extends AbstractReportUpdateSuccessUiTest {
    @Before
    public void setUp() {
        jobDbSetUp(jobModelWithoutDependents());
        this.setSkipDaoCheck(true);
        super.setUp();
    }

    @Test
    public void test() {
        super.test();
        assertThat(el(String.format(".report-list-%d-f .title-f", 0), withText(jobDao.getJobById(jobModel.getId()).getTitle()))).isDisplayed();
    }

    @Override
    public boolean getCopy() {
        return false;
    }
}
