package com.kwery.tests.services.job.launch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.PostgreSqlDockerRule;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.postoffice.Mail;
import ninja.postoffice.mock.PostofficeMockImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.models.JobModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class JobServiceLaunchJobPostgreSqlSuccessTest extends RepoDashTestBase {
    @Rule
    public PostgreSqlDockerRule postgreSqlDockerRule = new PostgreSqlDockerRule();

    protected JobModel jobModel;
    protected JobExecutionDao jobExecutionDao;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected Datasource datasource;

    protected int sqlQueryId0;
    protected int sqlQueryId1;

    protected MailService mailService;

    @Before
    public void setUpJobServiceJobSetUpAbstractTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setCronExpression("* * * * *");
        jobModel.setSqlQueries(new HashSet<>());
        jobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@moo.com"));

        datasource = postgreSqlDockerRule.getPostgreSqlDocker().datasource();
        datasource.setId(dbId());

        datasourceDbSetup(datasource);

        for (int i = 0; i < 2; ++i) {
            SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);

            if (i == 0) {
                sqlQueryId0 = sqlQueryModel.getId();
            } else {
                sqlQueryId1 = sqlQueryModel.getId();
            }

            sqlQueryModel.setQuery(getQuery());
            jobModel.getSqlQueries().add(sqlQueryModel);
        }

        jobDbSetUp(jobModel);

        sqlQueryDbSetUp(jobModel.getSqlQueries());

        jobEmailDbSetUp(jobModel);

        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            insertInto(JOB_SQL_QUERY_TABLE)
                                    .row()
                                    .column(ID_COLUMN, sqlQueryModel.getId())
                                    .column(JOB_ID_FK_COLUMN, jobModel.getId())
                                    .column(SQL_QUERY_ID_FK_COLUMN, sqlQueryModel.getId())
                                    .end()
                                    .build()
                    )
            ).launch();
        }

        jobExecutionDao = getInstance(JobExecutionDao.class);
        jobService = getInstance(JobService.class);
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
        mailService = getInstance(MailService.class);
    }

    protected void assertJobExecutionModel(JobExecutionModel.Status status, int jobId) {
        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(jobId, status);
        assertThat(jobExecutionModels, hasSize(1));
        JobExecutionModel jobExecution = jobExecutionModels.get(0);
        assertThat(jobExecution.getId(), greaterThan(0));
        assertThat(jobExecution.getExecutionId(), not(nullValue()));
        assertThat(jobExecution.getExecutionStart(), lessThan(jobExecution.getExecutionEnd()));
        assertThat(jobExecution.getStatus(), is(status));
        assertThat(jobExecution.getJobModel().getId(), is(jobId));
    }

    protected void assertJobExecutionModel(JobExecutionModel.Status status) {
        assertJobExecutionModel(status, jobModel.getId());
    }

    protected void assertJobExecutionModels(JobExecutionModel.Status status, int size) {
        assertJobExecutionModels(status, size, jobModel.getId());
    }

    protected void assertJobExecutionModels(JobExecutionModel.Status status, int size, int jobId) {
        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(jobId, status);
        assertThat(jobExecutionModels, hasSize(greaterThanOrEqualTo(size)));

        for (JobExecutionModel jobExecution : jobExecutionModels) {
            assertThat(jobExecution.getId(), greaterThan(0));
            assertThat(jobExecution.getExecutionId(), not(nullValue()));
            assertThat(jobExecution.getExecutionStart(), lessThan(jobExecution.getExecutionEnd()));
            assertThat(jobExecution.getStatus(), is(status));
            assertThat(jobExecution.getJobModel().getId(), is(jobId));
        }

    }

    protected void assertSqlQueryExecutionModel(int sqlQueryId, SqlQueryExecutionModel.Status status) {
        List<SqlQueryExecutionModel> sqlQueryExecutionModels = getSqlQueryExecutionModels(sqlQueryId, status);
        assertThat(sqlQueryExecutionModels, hasSize(1));

        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionModels.get(0);
        assertThat(sqlQueryExecution.getId(), greaterThan(0));
        assertThat(sqlQueryExecution.getExecutionId(), not(nullValue()));
        assertThat(sqlQueryExecution.getExecutionStart(), lessThan(sqlQueryExecution.getExecutionEnd()));
        assertThat(sqlQueryExecution.getStatus(), is(status));
        assertThat(sqlQueryExecution.getSqlQuery().getId(), is(sqlQueryId));

        if (status == SqlQueryExecutionModel.Status.SUCCESS) {
            assertThat(sqlQueryExecution.getResult(), is("[[\"table_name\"],[\"pg_depend\"]]"));
        } else if (status == SqlQueryExecutionModel.Status.FAILURE) {
            assertThat(sqlQueryExecution.getResult(), is("No database selected"));
        } else {
            assertThat(sqlQueryExecution.getResult(), nullValue());
        }
    }

    protected List<JobExecutionModel> getJobExecutionModels(JobExecutionModel.Status status) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(status));
        return jobExecutionDao.filter(filter);
    }

    protected List<JobExecutionModel> getJobExecutionModels(int jobId, JobExecutionModel.Status status) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(status));
        filter.setJobId(jobId);
        return jobExecutionDao.filter(filter);
    }

    protected List<JobExecutionModel> getJobExecutionModels(int jobId) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobId);
        return jobExecutionDao.filter(filter);
    }

    protected List<SqlQueryExecutionModel> getSqlQueryExecutionModels(int sqlQueryId, SqlQueryExecutionModel.Status status) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);
        filter.setStatuses(ImmutableList.of(status));
        return sqlQueryExecutionDao.filter(filter);
    }

    protected List<SqlQueryExecutionModel> getSqlQueryExecutionModels(SqlQueryExecutionModel.Status status) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(status));
        return sqlQueryExecutionDao.filter(filter);
    }

    protected String getQuery() {
        return "select table_name from information_schema.tables where table_name = 'pg_depend';";
    }

    @Test
    public void test() throws InterruptedException {
        jobService.launch(jobModel.getId());

        waitAtMost(1, MINUTES).until(() -> !getJobExecutionModels(JobExecutionModel.Status.SUCCESS).isEmpty());

        assertJobExecutionModel(JobExecutionModel.Status.SUCCESS);

        assertSqlQueryExecutionModel(sqlQueryId0, SqlQueryExecutionModel.Status.SUCCESS);
        assertSqlQueryExecutionModel(sqlQueryId1, SqlQueryExecutionModel.Status.SUCCESS);


        Mail mail = ((PostofficeMockImpl) mailService.getPostoffice()).getLastSentMail();
        assertThat(mail, notNullValue());
    }
}
