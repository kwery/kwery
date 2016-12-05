package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.JobExecutionModel.*;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

public class SqlQueryExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected JobModel jobModel;

    protected JobExecutionModel jobExecutionModel;

    @Before
    public void setUpSqlQueryExecutionDaoPersistTest() {
        Datasource datasource = datasource();
        sqlQuery = sqlQueryModel();
        sqlQuery.setDatasource(datasource);

        jobModel = jobModelWithoutDependents();
        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

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
                        insertInto(JobModel.JOB_TABLE)
                                .columns(JobModel.ID_COLUMN, JobModel.CRON_EXPRESSION_COLUMN ,JobModel.LABEL_COLUMN)
                                .values(jobModel.getId(), jobModel.getCronExpression(), jobModel.getLabel())
                                .build(),
                        insertInto(JobExecutionModel.TABLE)
                                .columns(JobExecutionModel.COLUMN_ID, COLUMN_EXECUTION_START, COLUMN_EXECUTION_END, COLUMN_EXECUTION_ID, COLUMN_STATUS,
                                        JobExecutionModel.JOB_ID_FK_COLUMN)
                                .values(jobExecutionModel.getId(), jobExecutionModel.getExecutionStart(), jobExecutionModel.getExecutionEnd(),
                                        jobExecutionModel.getExecutionId(), jobExecutionModel.getStatus(), jobModel.getId())
                                .build()
                )
        ).launch();

        sqlQueryExecutionDao = getInstance(SqlQueryExecutionDao.class);
    }

    @Test
    public void testPersist() throws DatabaseUnitException, SQLException, IOException {
        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionModelWithoutId();
        sqlQueryExecution.setSqlQuery(sqlQuery);
        sqlQueryExecution.setJobExecutionModel(jobExecutionModel);

        DozerBeanMapper mapper = new DozerBeanMapper();
        SqlQueryExecutionModel expected = mapper.map(sqlQueryExecution, SqlQueryExecutionModel.class);

        sqlQueryExecutionDao.save(sqlQueryExecution);
        expected.setId(sqlQueryExecution.getId());

        assertDbState(SqlQueryExecutionModel.TABLE, sqlQueryExecutionTable(expected));
    }

    @Test
    public void testPersisDoesNotAffectJobExecutionTable() throws Exception {
        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionModelWithoutId();
        sqlQueryExecution.setSqlQuery(sqlQuery);
        JobExecutionModel modifiedJobExecutionModel = jobExecutionModel();
        modifiedJobExecutionModel.setId(jobExecutionModel.getId());
        sqlQueryExecution.setJobExecutionModel(modifiedJobExecutionModel);
        sqlQueryExecutionDao.save(sqlQueryExecution);
        assertDbState(JobExecutionModel.TABLE, jobExecutionTable(this.jobExecutionModel));
    }

/*    @Test
    public void testUpdate() {
        sqlQueryExecutionDao.save(sqlQueryExecution);

        SqlQueryExecutionModel updated = sqlQueryExecutionDao.getByExecutionId(sqlQueryExecution.getExecutionId());
        updated.setExecutionEnd(100l);
        updated.setStatus(FAILURE);

        sqlQueryExecutionDao.save(updated);

        assertThat(updated.getId(), is(sqlQueryExecution.getId()));

        SqlQueryExecutionModel fromDb = sqlQueryExecutionDao.getById(updated.getId());

        assertThat(fromDb.getExecutionStart(), is(sqlQueryExecution.getExecutionStart()));
        assertThat(fromDb.getExecutionEnd(), is(100l));
        assertThat(fromDb.getStatus(), is(FAILURE));
    }*/
}
