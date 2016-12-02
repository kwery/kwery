package com.kwery.tests.dao.jobexecutiondao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobExecutionModel.*;
import static com.kwery.models.JobModel.JOB_SQL_QUERY_TABLE;
import static com.kwery.models.JobModel.SQL_QUERY_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_RESULT;
import static com.kwery.models.SqlQueryModel.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class JobExecutionDaoUpdateTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected JobModel jobModel;
    protected SqlQueryExecutionModel sqlQueryExecutionModel;

    protected JobExecutionModel jobExecutionModel;

    protected JobExecutionDao jobExecutionDao;

    protected JobModel jobModel0;

    @Before
    public void setUpJobExecutionDaoUpdateTest() {
        Datasource datasource = datasource();
        sqlQuery = sqlQueryModel();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setRecipientEmails(null);
        sqlQuery.setDependentQueries(null);
        sqlQuery.setCronExpression(null);

        jobModel = jobModel();
        jobModel.setSqlQueries(ImmutableSet.of(sqlQuery));

        jobModel0 = jobModel();
        jobModel0.setSqlQueries(ImmutableSet.of(sqlQuery));

        jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.setSqlQueryExecutionModels(ImmutableSet.of(sqlQueryExecutionModel));

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(Datasource.COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(),
                                        datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(ID_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(sqlQuery.getId(), sqlQuery.getLabel(), sqlQuery.getQuery(),
                                        sqlQuery.getDatasource().getId())
                                .build(),
                        insertInto(JobModel.JOB_TABLE)
                                .columns(ID_COLUMN, JobModel.CRON_EXPRESSION_COLUMN ,JobModel.LABEL_COLUMN)
                                .values(jobModel.getId(), jobModel.getCronExpression(), jobModel.getLabel())
                                .values(jobModel0.getId(), jobModel0.getCronExpression(), jobModel0.getLabel())
                                .build(),
                        insertInto(JOB_SQL_QUERY_TABLE)
                                .row()
                                .column(ID_COLUMN, sqlQuery.getId())
                                .column(JOB_ID_FK_COLUMN, jobModel.getId())
                                .column(SQL_QUERY_ID_FK_COLUMN, sqlQuery.getId())
                                .end()
                                .build(),
                        insertInto(JobExecutionModel.TABLE)
                                .columns(JobExecutionModel.COLUMN_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_STATUS,
                                        JobExecutionModel.JOB_ID_FK_COLUMN)
                                .values(jobExecutionModel.getId(), jobExecutionModel.getExecutionStart(), jobExecutionModel.getExecutionEnd(),
                                        jobExecutionModel.getExecutionId(), jobExecutionModel.getStatus(), jobExecutionModel.getJobModel().getId())
                                .build(),
                        insertInto(SqlQueryExecutionModel.TABLE)
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT,
                                        COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK, SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK)
                                .values(sqlQueryExecutionModel.getId(), sqlQueryExecutionModel.getExecutionEnd(), sqlQueryExecutionModel.getExecutionId(),
                                        sqlQueryExecutionModel.getExecutionStart(), sqlQueryExecutionModel.getResult(), sqlQueryExecutionModel.getStatus(),
                                        sqlQueryExecutionModel.getSqlQuery().getId(), sqlQueryExecutionModel.getJobExecutionModel().getId())
                                .build()
                )
        ).launch();

        jobExecutionDao = getInstance(JobExecutionDao.class);
    }

    @Test
    public void test() throws Exception {
        JobExecutionModel updated = jobExecutionDao.getById(jobExecutionModel.getId());
        updated.setExecutionStart(System.currentTimeMillis());
        updated.setExecutionEnd(System.currentTimeMillis());
        updated.setExecutionId(UUID.randomUUID().toString());
        updated.setStatus(Status.ONGOING);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobExecutionModel expected = mapper.map(updated, JobExecutionModel.class);

        jobExecutionDao.save(updated);

        DbUtil.assertDbState(JobExecutionModel.TABLE, DbUtil.jobExecutionTable(expected));
    }
}
