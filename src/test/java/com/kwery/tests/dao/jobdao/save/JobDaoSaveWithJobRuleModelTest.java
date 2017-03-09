package com.kwery.tests.dao.jobdao.save;

import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.models.JobRuleModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.ID_COLUMN;
import static com.kwery.models.JobModel.JOB_TABLE;
import static com.kwery.models.JobRuleModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.fooTable;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobTable;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static com.kwery.tests.util.TestUtil.jobRuleModelWithoutId;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class JobDaoSaveWithJobRuleModelTest extends RepoDashDaoTestBase {
    private JobDao jobDao;

    @Before
    public void setUp() {
        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        TestUtil.nullifyTimestamps(jobModel);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobModel expectedJobModel = mapper.map(jobModel, JobModel.class);
        expectedJobModel.setCreated(jobModel.getCreated());
        expectedJobModel.setUpdated(jobModel.getUpdated());

        JobRuleModel jobRuleModel = jobRuleModelWithoutId();
        JobRuleModel expectedJobRuleModel = mapper.map(jobRuleModel, JobRuleModel.class);
        expectedJobModel.setJobRuleModel(expectedJobRuleModel);

        jobModel.setJobRuleModel(jobRuleModel);

        long now = System.currentTimeMillis();

        jobModel = jobDao.save(jobModel);

        expectedJobModel.setId(jobModel.getId());
        expectedJobModel.setUpdated(jobModel.getUpdated());
        expectedJobModel.setCreated(jobModel.getCreated());

        expectedJobModel.setId(jobModel.getId());
        new DbTableAsserterBuilder(JOB_TABLE, jobTable(expectedJobModel)).columnsToIgnore(ID_COLUMN).build().assertTable();

        expectedJobRuleModel.setId(jobModel.getJobRuleModel().getId());

        new DbTableAsserterBuilder(JOB_RULE_TABLE, fooTable(expectedJobModel)).columnsToIgnore(JOB_RULE_ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_JOB_RULE_TABLE, fooTable(expectedJobModel)).columnsToIgnore(JOB_JOB_RULE_ID_COLUMN).build().assertTable();

        assertThat(jobModel.getCreated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
    }
}

