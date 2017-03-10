package com.kwery.tests.fluentlenium.job.reportlist.order;

import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.job.save.update.ReportUpdateSuccessUiTest;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.withText;

public class ReportListPostUpdateOrderUiTest extends ReportUpdateSuccessUiTest {
    private Set<Integer> existingJobIds;

    @Before
    public void setUp() {
        this.setSkipDaoCheck(true);
        super.setUp();
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);
        existingJobIds = ImmutableSet.of(jobModel.getId(), super.jobModel.getId());
    }

    @Test
    public void test() {
        super.test();
        page.waitForModalDisappearance();
        JobModel copiedJob = jobDao.getAllJobs().stream().filter(jobModel1 -> !existingJobIds.contains(jobModel1.getId())).collect(Collectors.toList()).get(0);
        assertThat(el(String.format(".report-list-%d-f .title-f", 0), withText(jobDao.getJobById(copiedJob.getId()).getTitle()))).isDisplayed();
    }
}
