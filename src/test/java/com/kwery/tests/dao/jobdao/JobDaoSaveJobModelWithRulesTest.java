package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.JobDao;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Test;

import static com.kwery.models.JobModel.*;
import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobRuleTable;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobTable;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;

public class JobDaoSaveJobModelWithRulesTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws Exception {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(true)));

        JobDao jobDao = getInstance(JobDao.class);
        jobDao.save(jobModel);

        new DbTableAsserterBuilder(JOB_TABLE, jobTable(jobModel)).columnsToIgnore(ID_COLUMN).build().assertTable();
        new DbTableAsserterBuilder(JOB_RULE_TABLE, jobRuleTable(jobModel)).columnsToIgnore(JOB_RULE_TABLE_ID_COLUMN).build().assertTable();
    }
}
