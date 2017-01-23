package com.kwery.tests.dao.jobexecutiondao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobExecutionModel.*;
import static com.kwery.models.JobModel.JOB_SQL_QUERY_TABLE;
import static com.kwery.models.JobModel.SQL_QUERY_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_QUERY_RUN_ID_FK;
import static com.kwery.models.SqlQueryExecutionModel.COLUMN_RESULT;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobExecutionDaoQueryTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected JobModel jobModel;
    protected SqlQueryExecutionModel sqlQueryExecutionModel;

    protected JobExecutionModel jobExecutionModel;

    protected JobExecutionDao jobExecutionDao;

    @Before
    public void setUpQueryRunExecutionDaoQueryTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQuery = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQuery);

        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(ImmutableList.of(sqlQuery));

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

        jobExecutionDao = getInstance(JobExecutionDao.class);
    }

    @Test
    public void testGetByExecutionId() {
        JobExecutionModel fromDb = jobExecutionDao.getByExecutionId(jobExecutionModel.getExecutionId());
        assertThat(fromDb, theSameBeanAs(jobExecutionModel));
    }

    @Test
    public void testGetById() {
        JobExecutionModel fromDb = jobExecutionDao.getById(jobExecutionModel.getId());
        assertThat(fromDb, theSameBeanAs(jobExecutionModel));
    }
}
