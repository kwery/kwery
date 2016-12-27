package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

public class SqlQueryExecutionDaoPersistTest extends RepoDashDaoTestBase {
    protected SqlQueryExecutionDao sqlQueryExecutionDao;
    protected SqlQueryModel sqlQuery;
    protected JobModel jobModel;

    protected JobExecutionModel jobExecutionModel;

    @Before
    public void setUpSqlQueryExecutionDaoPersistTest() {
        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQuery = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQuery);

        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);

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
    public void testPersistDoesNotAffectJobExecutionTable() throws Exception {
        SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutionModelWithoutId();
        sqlQueryExecution.setSqlQuery(sqlQuery);
        JobExecutionModel modifiedJobExecutionModel = jobExecutionModel();
        modifiedJobExecutionModel.setId(jobExecutionModel.getId());
        sqlQueryExecution.setJobExecutionModel(modifiedJobExecutionModel);
        sqlQueryExecutionDao.save(sqlQueryExecution);
        assertDbState(JobExecutionModel.TABLE, jobExecutionTable(this.jobExecutionModel));
    }
}
