package com.kwery.tests.services.job;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.services.job.JobExecutionSearchFilter;
import com.kwery.services.job.JobService;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;

import java.util.HashSet;
import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.JobModel.ID_COLUMN;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public abstract class JobServiceJobSetUpAbstractTest extends RepoDashTestBase {
    protected JobModel jobModel;
    protected JobExecutionDao jobExecutionDao;
    protected JobService jobService;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    protected int sqlQueryId0 = 1;
    protected int sqlQueryId1 = 2;

    @Before
    public void setUp() {
        jobModel = new JobModel();
        jobModel.setId(1);
        jobModel.setCronExpression("* * * * *");
        jobModel.setLabel("test");
        jobModel.setSqlQueries(new HashSet<>());

        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setLabel("mysql");
        datasource.setPassword("root");
        datasource.setUsername("root");
        datasource.setUrl("localhost");
        datasource.setPort(3306);
        datasource.setType(MYSQL);

        for (int sqlQueryId : ImmutableList.of(sqlQueryId0, sqlQueryId1)) {
            SqlQueryModel sqlQueryModel = new SqlQueryModel();
            sqlQueryModel.setId(sqlQueryId);
            sqlQueryModel.setLabel("select" + sqlQueryId);
            sqlQueryModel.setQuery(getQuery());
            sqlQueryModel.setDatasource(datasource);

            jobModel.getSqlQueries().add(sqlQueryModel);
        }

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                insertInto(JOB_TABLE)
                        .row()
                        .column(ID_COLUMN, jobModel.getId())
                        .column(JobModel.CRON_EXPRESSION_COLUMN, jobModel.getCronExpression())
                        .column(JobModel.LABEL_COLUMN, jobModel.getLabel())
                        .end()
                        .build()
        ).launch();

        for (SqlQueryModel sqlQueryModel : jobModel.getSqlQueries()) {
            new DbSetup(
                    new DataSourceDestination(getDatasource()),
                    Operations.sequenceOf(
                            insertInto(SQL_QUERY_TABLE)
                                    .row()
                                    .column(ID_COLUMN, sqlQueryModel.getId())
                                    .column(SqlQueryModel.LABEL_COLUMN, sqlQueryModel.getLabel())
                                    .column(QUERY_COLUMN, sqlQueryModel.getQuery())
                                    .column(DATASOURCE_ID_FK_COLUMN, sqlQueryModel.getDatasource().getId())
                                    .end()
                                    .build(),
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
    }

    protected void assertJobExecutionModel(JobExecutionModel.Status status) {
        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(status);
        assertThat(jobExecutionModels, hasSize(1));
        JobExecutionModel jobExecution = jobExecutionModels.get(0);
        assertThat(jobExecution.getId(), greaterThan(0));
        assertThat(jobExecution.getExecutionId(), not(nullValue()));
        assertThat(jobExecution.getExecutionStart(), lessThan(jobExecution.getExecutionEnd()));
        assertThat(jobExecution.getStatus(), is(status));
        assertThat(jobExecution.getJobModel().getId(), is(jobModel.getId()));
    }

    protected void assertJobExecutionModels(JobExecutionModel.Status status, int size) {
        List<JobExecutionModel> jobExecutionModels = getJobExecutionModels(status);
        assertThat(jobExecutionModels, hasSize(greaterThanOrEqualTo(size)));

        for (JobExecutionModel jobExecution : jobExecutionModels) {
            assertThat(jobExecution.getId(), greaterThan(0));
            assertThat(jobExecution.getExecutionId(), not(nullValue()));
            assertThat(jobExecution.getExecutionStart(), lessThan(jobExecution.getExecutionEnd()));
            assertThat(jobExecution.getStatus(), is(status));
            assertThat(jobExecution.getJobModel().getId(), is(jobModel.getId()));
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
            assertThat(sqlQueryExecution.getResult(), is("[[\"User\"],[\"root\"]]"));
        } else {
            assertThat(sqlQueryExecution.getResult(), nullValue());
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
                assertThat(sqlQueryExecution.getResult(), is("[[\"User\"],[\"root\"]]"));
            } else {
                assertThat(sqlQueryExecution.getResult(), nullValue());
            }
        }
    }

    protected List<JobExecutionModel> getJobExecutionModels(JobExecutionModel.Status status) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobModel.getId());
        filter.setStatuses(ImmutableList.of(status));
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
}
