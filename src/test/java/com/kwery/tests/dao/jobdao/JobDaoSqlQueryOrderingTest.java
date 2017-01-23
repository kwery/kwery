package com.kwery.tests.dao.jobdao;

import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobDaoSqlQueryOrderingTest extends RepoDashDaoTestBase {
    private JobDao jobDao;
    private JobModel jobModel;

    @Before
    public void setUp() {
        jobModel = jobModelWithoutIdWithoutDependents();

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        for (int i = 0; i < 3; ++i) {
            SqlQueryModel sqlQueryModel = sqlQueryModelWithoutId(datasource);
            jobModel.getSqlQueries().add(sqlQueryModel);
        }

        jobDao = getInstance(JobDao.class);

        jobModel = jobDao.save(jobModel);
    }

    @Test
    public void test() {
        JobModel jobModelFromDb = jobDao.getJobById(jobModel.getId());
        for (int i = 0; i < jobModel.getSqlQueries().size(); ++i) {
            assertThat(jobModelFromDb.getSqlQueries().get(i).getId(), is(jobModel.getSqlQueries().get(i).getId()));
        }
    }
}
