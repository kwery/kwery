package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobExecutionModel.*;
import static com.kwery.models.JobExecutionModel.JOB_ID_FK_COLUMN;
import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_RESULT;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected JobModel jobModel;
    protected SqlQueryExecutionModel sqlQueryExecutionModel;

    protected JobExecutionModel jobExecutionModel;

    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
        Datasource datasource = datasource();
        sqlQuery = sqlQueryModel();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setRecipientEmails(null);
        sqlQuery.setDependentQueries(null);
        sqlQuery.setCronExpression(null);

        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(ImmutableSet.of(sqlQuery));

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.setSqlQueryExecutionModels(ImmutableSet.of(sqlQueryExecutionModel));

        jobDbSetUp(jobModel);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(Datasource.COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(),
                                        datasource.getType(), datasource.getUrl(), datasource.getUsername())
                                .build(),
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(sqlQuery.getId(), sqlQuery.getLabel(), sqlQuery.getQuery(),
                                        sqlQuery.getDatasource().getId())
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
                                .columns(SqlQueryExecutionModel.COLUMN_ID, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_EXECUTION_START, COLUMN_RESULT, COLUMN_STATUS, COLUMN_QUERY_RUN_ID_FK, SqlQueryExecutionModel.COLUMN_JOB_EXECUTION_ID_FK)
                                .values(sqlQueryExecutionModel.getId(), sqlQueryExecutionModel.getExecutionEnd(), sqlQueryExecutionModel.getExecutionId(), sqlQueryExecutionModel.getExecutionStart(), sqlQueryExecutionModel.getResult(), sqlQueryExecutionModel.getStatus(), sqlQueryExecutionModel.getSqlQuery().getId(), sqlQueryExecutionModel.getJobExecutionModel().getId())
                                .build()
                )
        ).launch();

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void testGetByExecutionId() {
        SqlQueryExecutionModel fromDb = sqlQueryExecutionDao.getByExecutionId(sqlQueryExecutionModel.getExecutionId());

        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        assertThat(fromDb, theSameBeanAs(sqlQueryExecutionModel));
    }

    @Test
    public void testGetById() {
        SqlQueryExecutionModel fromDb = sqlQueryExecutionDao.getById(sqlQueryExecutionModel.getId());
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);
        assertThat(fromDb, theSameBeanAs(sqlQueryExecutionModel));
    }
}
