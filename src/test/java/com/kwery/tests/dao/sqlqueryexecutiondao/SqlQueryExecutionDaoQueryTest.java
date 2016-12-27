package com.kwery.tests.dao.sqlqueryexecutiondao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
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
        datasourceDbSetup(datasource);

        sqlQuery = sqlQueryModel(datasource);
        DbUtil.sqlQueryDbSetUp(sqlQuery);

        jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobModel.getSqlQueries().add(sqlQuery);
        jobSqlQueryDbSetUp(jobModel);

        jobExecutionModel = jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.setSqlQueryExecutionModels(ImmutableSet.of(sqlQueryExecutionModel));
        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);

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
