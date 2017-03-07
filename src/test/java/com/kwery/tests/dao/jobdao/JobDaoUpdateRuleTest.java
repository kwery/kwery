package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.kwery.models.JobModel.*;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class JobDaoUpdateRuleTest extends RepoDashDaoTestBase {
    private JobModel jobModel;
    private JobDao jobDao;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(true)));
        jobRuleDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testUpdate() throws Exception {
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(false)));
        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserter.DbTableAsserterBuilder(JOB_TABLE, jobTable(expected)).columnsToIgnore(ID_COLUMN).build().assertTable();
        new DbTableAsserter.DbTableAsserterBuilder(JOB_RULE_TABLE, jobRuleTable(expected)).columnsToIgnore(JOB_RULE_TABLE_ID_COLUMN).build().assertTable();

        assertThat(jobModel.getUpdated(), notNullValue());
    }

    @Test
    public void testEmptyRules() throws Exception {
        jobModel.setRules(new HashMap<>());
        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobModel = jobDao.save(jobModel);
        expected.setUpdated(jobModel.getUpdated());

        new DbTableAsserter.DbTableAsserterBuilder(JOB_TABLE, jobTable(expected)).columnsToIgnore(ID_COLUMN).build().assertTable();
        new DbTableAsserter.DbTableAsserterBuilder(JOB_RULE_TABLE, jobRuleTable(expected)).columnsToIgnore(JOB_RULE_TABLE_ID_COLUMN).build().assertTable();
        assertThat(jobModel.getUpdated(), notNullValue());
    }
}
