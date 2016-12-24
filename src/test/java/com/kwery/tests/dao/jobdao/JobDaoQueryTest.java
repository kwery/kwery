package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.UUID.randomUUID;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobDaoQueryTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel;
    protected Datasource datasource;

    private JobModel dependentJob0;

    @Before
    public void setUpJobDaoQueryTest() {
        dependentJob0 = jobModelWithoutDependents();
        jobDbSetUp(dependentJob0);

        JobModel dependentJob1 = jobModelWithoutDependents();
        jobDbSetUp(dependentJob1);

        jobModel = jobModelWithoutDependents();
        jobModel.setEmails(ImmutableSet.of(randomUUID().toString(), randomUUID().toString()));
        jobModel.getDependentJobs().addAll(ImmutableSet.of(dependentJob0, dependentJob1));

        datasource = datasource();

        datasourceDbSetup(datasource);

        jobModel.getSqlQueries().addAll(ImmutableSet.of(sqlQueryModel(datasource), sqlQueryModel(datasource)));

        dependentJob0.setDependsOnJob(jobModel);
        dependentJob1.setDependsOnJob(jobModel);

        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(jobModel.getSqlQueries());
        jobSqlQueryDbSetUp(jobModel);
        jobEmailDbSetUp(jobModel);
        jobDependentDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testGetJobById() {
        assertThat(jobDao.getJobById(jobModel.getId()), theSameBeanAs(jobModel));
        assertThat(jobDao.getJobById(jobModel.getId() + DB_START_ID + 100), nullValue());
    }

    @Test
    public void testGetJobByLabel() {
        assertThat(jobDao.getJobByLabel(jobModel.getLabel()), theSameBeanAs(jobModel));
        assertThat(jobDao.getJobByLabel(randomUUID().toString()), nullValue());
    }

    @Test
    public void testDependsOnJob() {
        assertThat(jobDao.getJobById(dependentJob0.getId()), theSameBeanAs(dependentJob0));
    }
}
