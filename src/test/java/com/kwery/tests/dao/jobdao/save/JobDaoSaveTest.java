package com.kwery.tests.dao.jobdao.save;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryEmailSettingModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.JobModel.*;
import static com.kwery.models.SqlQueryEmailSettingModel.SQL_QUERY_EMAIL_SETTING_TABLE;
import static com.kwery.models.SqlQueryModel.ID_COLUMN;
import static com.kwery.models.SqlQueryModel.SQL_QUERY_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.IsNull.notNullValue;
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

        long now = System.currentTimeMillis();

        jobDao.save(jobModel);

        expectedJobModel.setId(jobModel.getId());
        expectedJobModel.setCreated(jobModel.getCreated());
        expectedJobModel.setUpdated(jobModel.getUpdated());

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));

        assertThat(jobModel.getCreated(), greaterThanOrEqualTo(now));
        assertThat(jobModel.getUpdated(), greaterThanOrEqualTo(now));
    }

    @Test
    public void testWithSqlQuery() throws DatabaseUnitException, SQLException, IOException {
        JobModel jobModel = jobModelWithoutIdWithoutDependents();
        jobModel.setCreated(null);
        jobModel.setUpdated(null);

        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();

        JobModel expectedJobModel = dozerBeanMapper.map(jobModel, JobModel.class);

        SqlQueryModel sqlQueryModel0 = sqlQueryModelWithoutId(datasource);
        SqlQueryEmailSettingModel sqlQueryEmailSettingModel0 = sqlQueryEmailSettingModelWithoutId();
        sqlQueryModel0.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel0);
        SqlQueryModel expectedSqlQueryModel0 = dozerBeanMapper.map(sqlQueryModel0, SqlQueryModel.class);

        SqlQueryModel sqlQueryModel1 = sqlQueryModelWithoutId(datasource);
        SqlQueryEmailSettingModel sqlQueryEmailSettingModel1 = sqlQueryEmailSettingModelWithoutId();
        sqlQueryModel1.setSqlQueryEmailSettingModel(sqlQueryEmailSettingModel1);
        SqlQueryModel expectedSqlQueryModel1 = dozerBeanMapper.map(sqlQueryModel1, SqlQueryModel.class);

        jobModel.setSqlQueries(ImmutableList.of(sqlQueryModel0, sqlQueryModel1));

        long now = System.currentTimeMillis();

        jobDao.save(jobModel);

        expectedJobModel.setId(jobModel.getId());
        expectedSqlQueryModel0.setId(jobModel.getSqlQueries().iterator().next().getId());
        expectedJobModel.setCreated(jobModel.getCreated());
        expectedJobModel.setUpdated(jobModel.getUpdated());

        assertDbState(JOB_TABLE, jobTable(expectedJobModel));
        assertDbState(SQL_QUERY_TABLE, sqlQueryTable(ImmutableSet.of(expectedSqlQueryModel0, expectedSqlQueryModel1)), ID_COLUMN);

        new DbTableAsserterBuilder(JOB_SQL_QUERY_TABLE, jobSqlQueryTable(jobModel))
                .columnsToIgnore(JOB_SQL_QUERY_TABLE_UI_ORDER_COLUMN, JOB_SQL_QUERY_TABLE_ID_COLUMN).build().assertTable();

        new DbTableAsserterBuilder(SQL_QUERY_EMAIL_SETTING_TABLE, sqlQueryEmailSettingTable(expectedSqlQueryModel0, expectedSqlQueryModel1))
                .columnsToIgnore(SqlQueryEmailSettingModel.SQL_QUERY_EMAIL_SETTING_ID_COLUMN, SqlQueryEmailSettingModel.SQL_QUERY_SQL_QUERY_EMAIL_SETTING_ID_COLUMN).build().assertTable();

        assertThat(sqlQueryModel0.getId(), greaterThan(0));
        assertThat(sqlQueryModel1.getId(), greaterThan(0));

        assertThat(jobModel.getCreated(), greaterThan(now));
        assertThat(jobModel.getUpdated(), greaterThan(now));
    }
}
