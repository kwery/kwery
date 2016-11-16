package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import com.kwery.tests.util.RepoDashDaoTestBase;

import javax.persistence.PersistenceException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static com.kwery.tests.util.TestUtil.datasource;

public class DatasourceDaoPersistTest extends RepoDashDaoTestBase {
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

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void testNullValidation() {
        Datasource d = new Datasource();
        datasourceDao.save(d);
    }

    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void testMinimumLengthValidation() {
        Datasource d = new Datasource();
        d.setUrl("");
        d.setLabel("");
        d.setUsername("");
        datasourceDao.save(d);
    }
}
