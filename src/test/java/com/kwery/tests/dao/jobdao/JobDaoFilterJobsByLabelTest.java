package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
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
import static org.junit.Assert.assertThat;

public class JobDaoFilterJobsByLabelTest extends RepoDashDaoTestBase {
    private JobDao jobDao;
    private JobLabelModel jobLabelModel0;
    private JobModel jobModel0;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        jobModel0 = jobModelWithoutDependents();
        jobDbSetUp(jobModel0);

        jobModel1 = jobModelWithoutDependents();
        jobDbSetUp(jobModel1);

        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobModel0.setLabels(ImmutableSet.of(jobLabelModel0));
        jobJobLabelDbSetUp(jobModel0);

        jobModel1.setLabels(ImmutableSet.of(jobLabelModel0));
        jobJobLabelDbSetUp(jobModel1);

        JobModel jobModel2 = jobModelWithoutDependents();
        jobDbSetUp(jobModel2);

        JobLabelModel jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);
        jobModel2.setLabels(ImmutableSet.of(jobLabelModel1));
        jobJobLabelDbSetUp(jobModel2);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() {
        List<JobModel> jobs = jobDao.getJobsByJobLabelIds(ImmutableSet.of(jobLabelModel0.getId()));
        assertThat(jobs, hasSize(2));
        assertThat(jobs.stream().map(JobModel::getId).collect(toList()), containsInAnyOrder(jobModel0.getId(), jobModel1.getId()));
    }
}
