package com.kwery.tests.dao.sqlquerydao;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.SqlQueryDao;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;

import javax.persistence.PersistenceException;

import static org.junit.Assert.fail;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.queryRun;

public class SqlQueryDaoUniqueLabelTest extends RepoDashDaoTestBase {
    protected SqlQueryDao dao;
    protected SqlQuery persistedModel;
    protected Datasource datasource;

    @Before
    public void setUpQueryRunDaoUniqueLabelTest() {
        dao = getInstance(SqlQueryDao.class);
        persistedModel = queryRun();
        datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);
        persistedModel.setDatasource(datasource);
        dao.save(persistedModel);
    }

    @Test
    public void test() {
        SqlQuery duplicate = TestUtil.queryRun();
        duplicate.setDatasource(datasource);

        try {
            dao.save(duplicate);
        } catch (PersistenceException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                fail();
            }
        }
    }
}
