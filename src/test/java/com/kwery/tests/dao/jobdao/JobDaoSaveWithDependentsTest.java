package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobModel.JOB_CHILDREN_TABLE;
import static com.kwery.models.JobModel.JOB_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;

public class JobDaoSaveWithDependentsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;

    @Before
    public void setUpJobDaoSaveTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(new HashSet<>());

        jobDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        JobModel newJobModel = jobModelWithoutIdWithoutDependents();
        newJobModel.setSqlQueries(new HashSet<>());
        newJobModel.setChildJobs(new HashSet<>());
        newJobModel.getChildJobs().add(jobModel);

        newJobModel = jobDao.save(newJobModel);

        assertDbState(JOB_TABLE, jobTable(ImmutableList.of(jobModel, newJobModel)));
        assertDbState(JOB_CHILDREN_TABLE, jobDependentTable(newJobModel), "id");
    }
}
