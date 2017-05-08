package com.kwery.tests.dao.jobdao.update;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobDaoUpdateWithDependentsTest extends RepoDashDaoTestBase {
    protected JobDao jobDao;
    protected JobModel jobModel0;
    protected JobModel jobModel1;
    protected JobModel jobModel2;
    protected Datasource datasource;

    @Before
    public void setUpJobDaoUpdateWithDependentsTest() {
        jobModel0 = jobModelWithoutDependents();
        jobModel0.setParameterCsv("foo bar moo");
        jobModel1 = jobModelWithoutDependents();
        jobModel1.setParameterCsv("foo bar moo");
        jobModel2 = jobModelWithoutDependents();
        jobModel2.setParameterCsv("foo bar moo");

        jobDbSetUp(ImmutableList.of(jobModel0, jobModel1, jobModel2));

        jobModel0.setChildJobs(new HashSet<>());
        jobModel0.getChildJobs().add(jobModel1);

        jobDependentDbSetUp(jobModel0);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testAddDependent() throws Exception {
        long created = jobModel0.getCreated();
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        JobModel dependent = jobDao.getJobById(jobModel2.getId());
        toUpdate.getChildJobs().add(dependent);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        long now = System.currentTimeMillis();

        TestUtil.nullifyTimestamps(toUpdate);
        toUpdate = jobDao.save(toUpdate);

        new DbTableAsserterBuilder(JOB_CHILDREN_TABLE, jobDependentTable(expected))
                .columnsToCompare(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN).columnsToIgnore(JOB_CHILDREN_TABLE_ID_COLUMN).build().assertTable();

        assertThat(toUpdate.getUpdated(), greaterThanOrEqualTo(now));
        assertThat(toUpdate.getCreated(), is(created));
    }

    @Test
    public void testRemoveAndAddDependent() throws Exception {
        long created = jobModel0.getCreated();
        JobModel toUpdate = jobDao.getJobById(jobModel0.getId());
        toUpdate.setChildJobs(new HashSet<>());
        toUpdate.getChildJobs().add(jobDao.getJobById(jobModel2.getId()));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(toUpdate, JobModel.class);

        long now = System.currentTimeMillis();

        TestUtil.nullifyTimestamps(toUpdate);
        toUpdate = jobDao.save(toUpdate);

        new DbTableAsserterBuilder(JOB_CHILDREN_TABLE, jobDependentTable(expected))
                .columnsToCompare(JOB_CHILDREN_TABLE_CHILD_JOB_ID_FK_COLUMN).columnsToIgnore(JOB_CHILDREN_TABLE_ID_COLUMN).build().assertTable();

        assertThat(toUpdate.getUpdated(), greaterThanOrEqualTo(now));
        assertThat(toUpdate.getCreated(), is(created));
    }
}
