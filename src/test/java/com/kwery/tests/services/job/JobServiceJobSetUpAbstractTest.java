package com.kwery.tests.services.job;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.services.mail.MailService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.WiserRule;
import com.kwery.conf.KweryDirectory;
import org.apache.commons.mail.util.MimeMessageParser;
import org.junit.Before;
import org.junit.Rule;
import org.subethamail.wiser.WiserMessage;

import javax.mail.internet.MimeMessage;
import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.Messages.REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M;
import static com.kwery.tests.util.TestUtil.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public abstract class JobServiceJobSetUpAbstractTest extends RepoDashTestBase {
    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Rule
    public WiserRule wiserRule = new WiserRule();

    protected JobModel jobModel;
    protected JobExecutionDao jobExecutionDao;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected Datasource datasource;

    protected int sqlQueryId0;
    protected int sqlQueryId1;

    protected MailService mailService;
    private KweryDirectory kweryDirectory;
    protected JobDao jobDao;

    @Before
    public void setUpJobServiceJobSetUpAbstractTest() {
        jobModel = jobModelWithoutDependents();
        jobModel.setParameterCsv("");

        jobModel.setCronExpression("* * * * *");
        jobModel.setEmails(ImmutableSet.of("foo@bar.com", "goo@moo.com"));

        datasource = mysqlDockerRule.getMySqlDocker().datasource();
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
        jobSqlQueryDbSetUp(jobModel);

        smtpConfigurationDbSetUp(wiserRule.smtpConfiguration());
        emailConfigurationDbSet(wiserRule.emailConfiguration());

        jobExecutionDao = getInstance(JobExecutionDao.class);
        jobService = getInstance(JobService.class);
        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
        mailService = getInstance(MailService.class);

        jobDao = getInstance(JobDao.class);

        kweryDirectory = getInstance(KweryDirectory.class);
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
            assertThat(kweryDirectory.getContent(sqlQueryExecution.getResultFileName()), is(String.join(System.lineSeparator(), "\"User\"", "\"root\"")));
        } else if (status == SqlQueryExecutionModel.Status.FAILURE) {
            assertThat(sqlQueryExecution.getExecutionError(), is("No database selected"));
        } else {
            assertThat(sqlQueryExecution.getExecutionError(), nullValue());
        }
    }

    protected void assertSqlQueryExecutionModels(int sqlQueryId, SqlQueryExecutionModel.Status status, int size) {
        List<SqlQueryExecutionModel> sqlQueryExecutionModels = getSqlQueryExecutionModels(sqlQueryId, status);
        assertThat(sqlQueryExecutionModels, hasSize(greaterThanOrEqualTo(size)));

        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutionModels) {
            assertThat(sqlQueryExecution.getId(), greaterThan(0));
            assertThat(sqlQueryExecution.getExecutionId(), not(nullValue()));
            assertThat(sqlQueryExecution.getExecutionStart(), lessThan(sqlQueryExecution.getExecutionEnd()));
            assertThat(sqlQueryExecution.getStatus(), is(status));
            assertThat(sqlQueryExecution.getSqlQuery().getId(), is(sqlQueryId));

            if (status == SqlQueryExecutionModel.Status.SUCCESS) {
                assertThat(kweryDirectory.getContent(sqlQueryExecution.getResultFileName()), is(String.join(System.lineSeparator(), "\"User\"", "\"root\"")));
            } else if (status == SqlQueryExecutionModel.Status.FAILURE) {
                assertThat(sqlQueryExecution.getExecutionError(), is("No database selected"));
            } else {
                assertThat(sqlQueryExecution.getExecutionError(), nullValue());
            }
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

    protected abstract String getQuery();

    public void assertReportEmailExists() throws Exception {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        for (WiserMessage wiserMessage : wiserRule.wiser().getMessages()) {
            MimeMessage mimeMessage = wiserMessage.getMimeMessage();
            MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
            assertThat(mimeMessageParser.getHtmlContent(), notNullValue());
            assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(false));
        }
    }

    public void assertEmailDoesNotExists() {
        assertThat(wiserRule.wiser().getMessages().isEmpty(), is(true));
    }

    public void assertReportFailureAlertEmailExists() throws Exception {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(() -> !wiserRule.wiser().getMessages().isEmpty());

        for (WiserMessage wiserMessage : wiserRule.wiser().getMessages()) {
            MimeMessage mimeMessage = wiserMessage.getMimeMessage();
            MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
            assertThat(mimeMessageParser.getHtmlContent(), notNullValue());
            assertThat(mimeMessageParser.getAttachmentList().isEmpty(), is(true));
            assertThat(mimeMessageParser.getSubject(), containsString(REPORT_GENERATION_FAILURE_ALERT_EMAIL_SUBJECT_M));
        }
    }
}
