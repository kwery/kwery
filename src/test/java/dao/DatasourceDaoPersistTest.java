package dao;

import models.Datasource;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatasourceDaoPersistTest extends BaseDatasourceDaoPersistTest {
    private DatasourceDao datasourceDao;

    @Before
    public void before() {
        datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test
    public void testPersist() {
        Datasource d = datasource();
        datasourceDao.save(d);
        Integer id = d.getId();
        assertNotNull("Persisted datasource has an id", id != null && id > 0);
    }

    @Test
    public void testUniqueLabel() {
        Datasource d = datasource();
        datasourceDao.save(d);

        Datasource newD = datasource();
        try {
            datasourceDao.save(newD);
        } catch (PersistenceException e) {
            if (!(e.getCause() instanceof ConstraintViolationException)) {
                assertTrue("Unique label condition failed", false);
            }
        }
    }
}
