package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.datasource;

public class DatasourceDaoUpdateTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    private Datasource datasource = null;


    @Before
    public void setUpDatasourceDaoUpdateTest() {
        datasource = datasource();
        datasourceDbSetup(datasource);

        datasourceDao = getInstance(DatasourceDao.class);
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
}
