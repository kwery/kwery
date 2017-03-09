package com.kwery.tests.dao.jobdao.update;

import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.models.JobRuleModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.ID_COLUMN;
import static com.kwery.models.JobModel.JOB_TABLE;
import static com.kwery.models.JobRuleModel.JOB_JOB_RULE_ID_COLUMN;
import static com.kwery.models.JobRuleModel.JOB_JOB_RULE_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.jobRuleModel;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobDaoUpdateJobRuleModelTest extends RepoDashDaoTestBase {
    private JobModel jobModel;
    private JobRuleModel jobRuleModel;
    private JobDao jobDao;
    private long created;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobRuleModel = jobRuleModel();
        jobModel.setJobRuleModel(jobRuleModel);
        fooDbSetUp(jobModel);

        created = jobModel.getCreated();
        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        JobRuleModel updated = jobRuleModel();
        updated.setId(jobRuleModel.getId());
        jobModel.setJobRuleModel(updated);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expected = mapper.map(jobModel, JobModel.class);

        long now = System.currentTimeMillis();

        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserterBuilder(JOB_TABLE, jobTable(expected)).columnsToIgnore(ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JobRuleModel.JOB_RULE_TABLE, fooTable(expected)).build().assertTable();
        new DbTableAsserterBuilder(JOB_JOB_RULE_TABLE, fooTable(expected)).columnsToIgnore(JOB_JOB_RULE_ID_COLUMN).build().assertTable();

        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getCreated(), is(created));
    }
}
