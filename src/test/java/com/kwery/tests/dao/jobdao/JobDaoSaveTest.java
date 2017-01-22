package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModelWithoutId;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class JobDaoSaveTest extends RepoDashTestBase {
    protected JobDao jobDao;
    protected Datasource datasource;

    @Before
    public void setUpJobDaoSaveTest() {
        datasource = TestUtil.datasource();
        datasourceDbSetup(datasource);
        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        JobModel expectedJobModel = new DozerBeanMapper().map(jobModel, JobModel.class);

        jobDao.save(jobModel);
        expectedJobModel.setId(jobModel.getId());
        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
    }

    @Test
    public void testWithSqlQuery() throws DatabaseUnitException, SQLException, IOException {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();

        JobModel expectedJobModel = dozerBeanMapper.map(jobModel, JobModel.class);

        SqlQueryModel sqlQueryModel0 = sqlQueryModelWithoutId(datasource);
        SqlQueryModel expectedSqlQueryModel0 = dozerBeanMapper.map(sqlQueryModel0, SqlQueryModel.class);

        SqlQueryModel sqlQueryModel1 = sqlQueryModelWithoutId(datasource);
        SqlQueryModel expectedSqlQueryModel1 = dozerBeanMapper.map(sqlQueryModel1, SqlQueryModel.class);

        jobModel.setSqlQueries(ImmutableList.of(sqlQueryModel0, sqlQueryModel1));

        jobDao.save(jobModel);

        expectedJobModel.setId(jobModel.getId());
        expectedSqlQueryModel0.setId(jobModel.getSqlQueries().iterator().next().getId());

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
        assertDbState(SQL_QUERY_TABLE, sqlQueryTable(ImmutableSet.of(expectedSqlQueryModel0, expectedSqlQueryModel1)), ID_COLUMN);

        new DbTableAsserter.DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(jobModel))
                .columnsToIgnore(JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN, JOB_SQL_QUERY_TABLE_ID_COLUMN).build().assertTable();

        assertThat(sqlQueryModel0.getId(), greaterThan(0));
        assertThat(sqlQueryModel1.getId(), greaterThan(0));
    }
}
