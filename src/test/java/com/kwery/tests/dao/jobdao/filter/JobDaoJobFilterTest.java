package com.kwery.tests.dao.jobdao.filter;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.services.job.JobSearchFilter;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class JobDaoJobFilterTest extends RepoDashDaoTestBase {
    private JobDao jobDao;
    private JobLabelModel jobLabelModel;
    private JobModel jobModel0;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        jobModel0 = jobModelWithoutDependents();
        jobDbSetUp(jobModel0);

        jobModel1 = jobModelWithoutDependents();
        jobDbSetUp(jobModel1);

        JobModel jobModel2 = jobModelWithoutDependents();
        jobDbSetUp(jobModel2);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobModel0.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel0);

        jobModel1.setLabels(ImmutableSet.of(jobLabelModel));
        jobJobLabelDbSetUp(jobModel1);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() {
        JobSearchFilter jobSearchFilter = new JobSearchFilter();
        jobSearchFilter.setJobLabelIds(ImmutableSet.of(jobLabelModel.getId()));
        jobSearchFilter.setPageNo(0);
        jobSearchFilter.setResultCount(100);

        List<JobModel> jobs = jobDao.filterJobs(jobSearchFilter);

        assertThat(jobs, hasSize(2));

        assertThat(jobs.stream().map(JobModel::getId).collect(toList()), containsInAnyOrder(jobModel0.getId(), jobModel1.getId()));
    }

    @Test
    public void testPagination() {
        JobSearchFilter jobSearchFilter = new JobSearchFilter();
        jobSearchFilter.setJobLabelIds(ImmutableSet.of(jobLabelModel.getId()));
        jobSearchFilter.setPageNo(0);
        jobSearchFilter.setResultCount(1);

        List<JobModel> jobs = jobDao.filterJobs(jobSearchFilter);

        assertThat(jobs, hasSize(1));

        JobModel firstPageJobModel = jobs.get(0);

        jobSearchFilter.setPageNo(1);

        jobs = jobDao.filterJobs(jobSearchFilter);

        assertThat(jobs, hasSize(1));

        assertThat(jobs.get(0).getId(), not(firstPageJobModel.getId()));
    }
}
