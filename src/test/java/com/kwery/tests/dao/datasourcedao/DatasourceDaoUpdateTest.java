package com.kwery.tests.dao.datasourcedao;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.sqlQueryDbSetUp;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.jobModelWithoutIdWithoutDependents;
import static com.kwery.tests.util.TestUtil.sqlQueryModel;

public class DatasourceDaoUpdateTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    private Datasource datasource = null;
    protected JobDao jobDao;


    @Before
    public void setUpDatasourceDaoUpdateTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        datasourceDao = getInstance(DatasourceDao.class);
        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void test() throws Exception {
        Datasource updated = datasourceDao.getById(datasource.getId());

        updated.setLabel("foo");
        updated.setPort(3307);
        updated.setUrl("bar.com");
        updated.setUsername("newUsername");
        updated.setPassword("newPassword");

        Datasource expected = new DozerBeanMapper().map(updated, Datasource.class);

        datasourceDao.update(updated);

        new DbTableAsserterBuilder(Datasource.TABLE, DbUtil.datasourceTable(expected)).build().assertTable();
    }

    //Test case for - https://github.com/kwery/kwery/issues/10
    @Test
    public void testUpdateWithDependencies() throws DatabaseUnitException, SQLException, IOException {
        Datasource d = datasourceDao.getById(datasource.getId());

        SqlQueryModel s0 = sqlQueryModel(datasource);
        s0.setId(null);

        SqlQueryModel s1 = sqlQueryModel(datasource);
        s1.setId(null);

        JobModel j = jobModelWithoutIdWithoutDependents();
        j.setSqlQueries(ImmutableList.of(s0, s1));

        jobDao.save(j);

        d.setLabel("foo");
        d.setPort(3307);
        d.setUrl("bar.com");
        d.setUsername("newUsername");
        d.setPassword("newPassword");

        Datasource e = new DozerBeanMapper().map(d, Datasource.class);

        datasourceDao.update(d);

        new DbTableAsserterBuilder(Datasource.TABLE, DbUtil.datasourceTable(e)).build().assertTable();
    }
}
