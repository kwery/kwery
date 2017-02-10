package com.kwery.tests.dao.jobexecutiondao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobExecutionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.*;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.kwery.models.JobExecutionModel.Status;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

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
        datasourceDbSetup(datasource);

        sqlQuery = sqlQueryModel(datasource);
        sqlQueryDbSetUp(sqlQuery);

        jobModel = jobModelWithoutDependents();
        jobModel.setSqlQueries(ImmutableList.of(sqlQuery));

        jobModel0 = jobModelWithoutDependents();
        jobModel0.setSqlQueries(ImmutableList.of(sqlQuery));

        jobExecutionModel = TestUtil.jobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        sqlQueryExecutionModel = sqlQueryExecutionModel();
        sqlQueryExecutionModel.setSqlQuery(sqlQuery);
        sqlQueryExecutionModel.setJobExecutionModel(jobExecutionModel);

        jobExecutionModel.setSqlQueryExecutionModels(ImmutableSet.of(sqlQueryExecutionModel));

        jobDbSetUp(ImmutableList.of(jobModel, jobModel0));

        jobSqlQueryDbSetUp(jobModel);
        jobExecutionDbSetUp(jobExecutionModel);
        sqlQueryExecutionDbSetUp(sqlQueryExecutionModel);

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
