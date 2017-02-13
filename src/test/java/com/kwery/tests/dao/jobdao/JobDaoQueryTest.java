package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.models.JobRuleModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.Rules.EMPTY_REPORT_NO_EMAIL;
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
        jobModel.setFailureAlertEmails(ImmutableSet.of(randomUUID().toString(), randomUUID().toString()));
        jobModel.getChildJobs().addAll(ImmutableSet.of(dependentJob0, dependentJob1));

        datasource = datasource();

        datasourceDbSetup(datasource);

        jobModel.getSqlQueries().addAll(Lists.newArrayList(sqlQueryModel(datasource), sqlQueryModel(datasource)));

        dependentJob0.setParentJob(jobModel);
        dependentJob1.setParentJob(jobModel);

        jobDbSetUp(jobModel);
        sqlQueryDbSetUp(jobModel.getSqlQueries());
        jobSqlQueryDbSetUp(jobModel);
        jobEmailDbSetUp(jobModel);
        jobFailureAlertEmailDbSetUp(jobModel);
        jobDependentDbSetUp(jobModel);

        jobModel.setRules(ImmutableMap.of(EMPTY_REPORT_NO_EMAIL, String.valueOf(true)));
        jobRuleDbSetUp(jobModel);

        JobLabelModel jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobModel.getLabels().add(jobLabelModel);

        jobJobLabelDbSetUp(jobModel);

        JobRuleModel jobRuleModel = jobRuleModel();
        jobModel.setJobRuleModel(jobRuleModel);
        fooDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testGetJobById() {
        JobModel jobModelFromDb = jobDao.getJobById(jobModel.getId());
        assertThat(jobModelFromDb, theSameBeanAs(jobModel));
        assertThat(jobDao.getJobById(jobModel.getId() + DB_START_ID + 100), nullValue());
    }

    @Test
    public void testDependsOnJob() {
        assertThat(jobDao.getJobById(dependentJob0.getId()), theSameBeanAs(dependentJob0));
    }

    @Test
    public void testGetJobByName() {
        assertThat(jobDao.getJobByName(jobModel.getName()), theSameBeanAs(jobModel));
        assertThat(jobDao.getJobByName(randomUUID().toString()), nullValue());
    }
}
