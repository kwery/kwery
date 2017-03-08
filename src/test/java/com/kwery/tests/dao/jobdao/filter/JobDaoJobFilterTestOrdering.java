package com.kwery.tests.dao.jobdao.filter;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.services.job.JobSearchFilter;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobDaoJobFilterTestOrdering extends RepoDashDaoTestBase {
    private JobDao jobDao;

    @Before
    public void setUp() {
        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testOnlyCreated() {
        JobModel jobModel0 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel0);

        JobModel jobModel1 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel1);

        JobModel jobModel2 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel2);

        List<JobModel> jobModels = jobDao.filterJobs(new JobSearchFilter());

        assertThat(jobModels.stream().map(JobModel::getId).collect(toList()), is(ImmutableList.of(jobModel2.getId(), jobModel1.getId(), jobModel0.getId())));
    }

    @Test
    public void testCreatedAndUpdated() {
        JobModel jobModel0 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel0);

        JobModel jobModel1 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel1);

        JobModel jobModel2 = jobModelWithoutIdWithoutDependents();
        jobDao.save(jobModel2);

        jobDao.save(jobModel1);
        jobDao.save(jobModel2);
        jobDao.save(jobModel0);

        List<JobModel> jobModels = jobDao.filterJobs(new JobSearchFilter());

        assertThat(jobModels.stream().map(JobModel::getId).collect(toList()), is(ImmutableList.of(jobModel0.getId(), jobModel2.getId(), jobModel1.getId())));
    }
}
