package dao.queryrundao;

import dao.DatasourceDao;
import dao.QueryRunDao;
import models.Datasource;
import models.QueryRun;
import ninja.NinjaDaoTestBase;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import util.TestUtil;

import javax.persistence.PersistenceException;

import static org.junit.Assert.fail;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRun;

public class QueryRunDaoUniqueLabelTest extends NinjaDaoTestBase {
    protected QueryRunDao dao;
    protected QueryRun persistedModel;
    protected Datasource datasource;

    @Before
    public void setUpQueryRunDaoUniqueLabelTest() {
        dao = getInstance(QueryRunDao.class);
        persistedModel = queryRun();
        datasource = datasource();
        getInstance(DatasourceDao.class).save(datasource);
        persistedModel.setDatasource(datasource);
        dao.save(persistedModel);
    }

    @Test
    public void test() {
        QueryRun duplicate = TestUtil.queryRun();
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
