package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.UUID.randomUUID;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobDaoQueryTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel0;
    protected Datasource datasource;

    @Before
    public void setUpJobDaoQueryTest() {
        JobModel jobModel1 = jobModelWithoutDependents();
        jobModel1.setEmails(new HashSet<>());
        jobModel1.setSqlQueries(new HashSet<>());
        jobDbSetUp(jobModel1);

        JobModel jobModel2 = jobModelWithoutDependents();
        jobModel2.setEmails(new HashSet<>());
        jobModel2.setSqlQueries(new HashSet<>());
        jobDbSetUp(jobModel2);

        jobModel0 = jobModelWithoutDependents();
        jobModel0.setSqlQueries(new HashSet<>());
        jobModel0.setEmails(ImmutableSet.of(randomUUID().toString(), randomUUID().toString()));
        jobModel0.getDependentJobs().addAll(ImmutableSet.of(jobModel1, jobModel2));

        datasource = datasource();

        datasourceDbSetup(datasource);

        jobModel0.getSqlQueries().addAll(ImmutableSet.of(sqlQueryModel(datasource), sqlQueryModel(datasource)));

        jobDbSetUp(jobModel0);
        sqlQueryDbSetUp(jobModel0.getSqlQueries());
        jobSqlQueryDbSetUp(jobModel0);
        jobEmailDbSetUp(jobModel0);
        jobDependentDbSetUp(jobModel0);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testGetJobById() {
        assertThat(jobDao.getJobById(jobModel0.getId()), theSameBeanAs(jobModel0));
        assertThat(jobDao.getJobById(jobModel0.getId() + 1), nullValue());
    }

    @Test
    public void testGetJobByLabel() {
        assertThat(jobDao.getJobByLabel(jobModel0.getLabel()), theSameBeanAs(jobModel0));
        assertThat(jobDao.getJobByLabel(randomUUID().toString()), nullValue());
    }
}
