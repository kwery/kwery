package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.JOB_EMAIL_ID_COLUMN;
import static com.kwery.models.JobModel.JOB_EMAIL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobDaoUpdateEmailTest extends RepoDashDaoTestBase {
    protected JobModel jobModel;
    protected JobDao jobDao;

    @Before
    public void setUpJobDaoUpdateEmailTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.getEmails().addAll(ImmutableSet.of("foo@goo.com", "bar@foo.com"));

        jobDbSetUp(jobModel);
        jobEmailDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testEmptyEmails() throws Exception {
        jobModel.getEmails().clear();

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_EMAIL_TABLE, jobEmailTable(expected)).columnToIgnore(JOB_EMAIL_ID_COLUMN).build().assertTable();
        assertThat(expected.getUpdated(), notNullValue());
    }

    @Test
    public void testAddAndDeleteEmails() throws Exception {
        jobModel.getEmails().remove("foo@boo.com");
        jobModel.getEmails().add("moo@choo.com");

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_EMAIL_TABLE, jobEmailTable(expected)).columnToIgnore(JOB_EMAIL_ID_COLUMN).build().assertTable();
        assertThat(expected.getUpdated(), notNullValue());
    }
}
