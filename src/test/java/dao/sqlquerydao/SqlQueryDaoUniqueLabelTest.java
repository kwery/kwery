package dao.sqlquerydao;

import dao.DatasourceDao;
import dao.SqlQueryDao;
import models.Datasource;
import models.SqlQuery;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;
import util.TestUtil;

import javax.persistence.PersistenceException;

import static org.junit.Assert.fail;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

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
