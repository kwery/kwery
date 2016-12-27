package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.JOB_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobTable;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class JobDaoDeleteWithMultipleJobsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected JobModel jobModelToDelete;

    @Before
    public void setUpJobDaoDeleteWithMultipleJobsTest() {
        jobModel = jobModelWithoutDependents();
        jobModelToDelete = jobModelWithoutDependents();

        jobDbSetUp(ImmutableList.of(jobModel, jobModelToDelete));

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        jobDao.delete(jobModelToDelete.getId());
        new DbTableAsserterBuilder(JOB_TABLE, jobTable(jobModel)).build().assertTable();
    }
}
