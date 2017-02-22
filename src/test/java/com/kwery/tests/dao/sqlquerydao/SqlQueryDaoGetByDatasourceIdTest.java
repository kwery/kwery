package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SqlQueryDaoGetByDatasourceIdTest extends RepoDashDaoTestBase {
    private Datasource datasource;
    private SqlQueryModel sqlQueryModel;
    private SqlQueryDao sqlQueryDao;

    @Before
    public void setUp() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        sqlQueryModel = sqlQueryModel(datasource);
        DbUtil.sqlQueryDbSetUp(sqlQueryModel);

        JobModel jobModel = jobModelWithoutDependents();
        jobModel.getSqlQueries().add(sqlQueryModel);

        jobDbSetUp(jobModel);
        jobSqlQueryDbSetUp(jobModel);

        sqlQueryDao = getInstance(SqlQueryDao.class);
    }

    @Test
    public void test() {
        List<SqlQueryModel> models = sqlQueryDao.getSqlQueriesWithDatasourceId(datasource.getId());
        assertThat(models, hasSize(1));
    }
}
