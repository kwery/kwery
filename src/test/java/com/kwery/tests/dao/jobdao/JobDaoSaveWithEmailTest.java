package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobDaoSaveWithEmailTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws Exception {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        jobModel.getEmails().addAll(ImmutableSet.of("foo@bar.com", "boo@goo.com"));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(jobModel, JobModel.class);

        jobModel = getInstance(JobDao.class).save(jobModel);
        expected.setId(jobModel.getId());
        expected.setCreated(jobModel.getCreated());

        new DbTableAsserterBuilder(JobModel.JOB_TABLE, DbUtil.jobTable(expected)).build().assertTable();
        new DbTableAsserterBuilder(JobModel.JOB_EMAIL_TABLE, DbUtil.jobEmailTable(expected)).columnToIgnore(JobModel.JOB_EMAIL_ID_COLUMN).build().assertTable();

        assertThat(jobModel.getCreated(), notNullValue());
    }
}
