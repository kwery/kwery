package com.kwery.tests.dao.jobexecutiondao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
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

        jobSqlQueryDbSetUp(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);
        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);

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
