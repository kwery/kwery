package com.kwery.tests.dao.jobdao.save;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobFailureAlertEmailTable;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class JobDaoSaveWithFailureAlertEmailTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws Exception {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        TestUtil.nullifyTimestamps(jobModel);

        jobModel.getFailureAlertEmails().addAll(ImmutableSet.of("foo@bar.com", "boo@goo.com"));

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(jobModel, JobModel.class);
        expected.setCreated(jobModel.getCreated());

        long now = System.currentTimeMillis();

        jobModel = getInstance(JobDao.class).save(jobModel);
        expected.setId(jobModel.getId());
        expected.setCreated(jobModel.getCreated());
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_TABLE, DbUtil.jobTable(expected)).build().assertTable();
        new DbTableAsserterBuilder(JOB_FAILURE_ALERT_EMAIL_TABLE, jobFailureAlertEmailTable(expected))
                .columnToIgnore(JOB_FAILURE_ALERT_EMAIL_ID_COLUMN).build().assertTable();

        assertThat(jobModel.getCreated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
    }
}
