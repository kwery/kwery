package com.kwery.tests.services.job.launch.jobrulemodel;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobExecutionDao;
import com.kwery.models.*;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.ReportUtil;
import org.junit.Before;
import org.junit.Rule;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

public abstract class JobServiceLaunchJobJobRuleModelAbstractTest extends RepoDashTestBase {
    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();
    private JobExecutionDao jobExecutionDao;

    protected JobModel jobModel;
    protected JobService jobService;

    public abstract String getQuery0();

    public abstract String getQuery1();

    public abstract String getQuery2();

    public abstract JobRuleModel getJobRuleMode();

    @Before
    public void setUp() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobDbSetUp(jobModel);

        Datasource datasource = TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL);
        datasource.setId(DbUtil.dbId());
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel0 = sqlQueryModel(datasource);
        sqlQueryModel0.setQuery(getQuery0());
        sqlQueryDbSetUp(sqlQueryModel0);
        jobModel.getSqlQueries().add(sqlQueryModel0);

        SqlQueryModel sqlQueryModel1 = sqlQueryModel(datasource);
        sqlQueryModel1.setQuery(getQuery1());
        sqlQueryDbSetUp(sqlQueryModel1);
        jobModel.getSqlQueries().add(sqlQueryModel1);

        SqlQueryModel sqlQueryModel2 = sqlQueryModel(datasource);
        sqlQueryModel2.setQuery(getQuery2());
        sqlQueryDbSetUp(sqlQueryModel2);
        jobModel.getSqlQueries().add(sqlQueryModel2);

        jobSqlQueryDbSetUp(jobModel);

        jobModel.setJobRuleModel(getJobRuleMode());
        fooDbSetUp(jobModel);

        jobExecutionDao = getInstance(JobExecutionDao.class);
        jobService = getInstance(JobService.class);
    }

    protected List<JobExecutionModel> getJobExecutionModels(int jobId) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobId);
        filter.setStatuses(ImmutableList.of(JobExecutionModel.Status.SUCCESS, JobExecutionModel.Status.FAILURE, JobExecutionModel.Status.KILLED));
        return jobExecutionDao.filter(filter);
    }

    protected void assertQueriesWereExecutedSequentially(int expectedNoOfSqlQueryExecutions) {
        for (JobExecutionModel model : getJobExecutionModels(jobModel.getId())) {
            List<SqlQueryExecutionModel> executionModels = ReportUtil.orderedExecutions(model);

            assertThat(executionModels, hasSize(expectedNoOfSqlQueryExecutions));

            if (expectedNoOfSqlQueryExecutions <= 2) {
                SqlQueryExecutionModel first = executionModels.get(0);
                SqlQueryExecutionModel second = executionModels.get(1);
                assertThat(first.getExecutionEnd(), lessThan(second.getExecutionStart()));
            }

            if (expectedNoOfSqlQueryExecutions > 2) {
                SqlQueryExecutionModel second = executionModels.get(1);
                SqlQueryExecutionModel third = executionModels.get(2);
                assertThat(second.getExecutionEnd(), lessThan(third.getExecutionStart()));
            }
        }
    }

    protected void waitUntilExecutions(long value, TimeUnit timeUnit, int noOfExecutions) {
        waitAtMost(value, timeUnit).until(() -> getJobExecutionModels(jobModel.getId()).size() >= noOfExecutions);
    }
}
