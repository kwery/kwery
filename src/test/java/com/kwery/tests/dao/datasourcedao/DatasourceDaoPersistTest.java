package com.kwery.tests.dao.datasourcedao;

import com.kwery.dao.DatasourceDao;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceTable;
import static com.kwery.tests.util.TestUtil.datasource;
import static com.kwery.tests.util.TestUtil.datasourceWithoutId;
import static org.junit.Assert.assertTrue;

public class DatasourceDaoPersistTest extends RepoDashDaoTestBase {
    private DatasourceDao datasourceDao;

    @Before
    public void before() {
        datasourceDao = getInstance(DatasourceDao.class);
    }

    @Test
    public void testPersist() throws Exception {
        Datasource d = datasourceWithoutId();
        Datasource expected = new DozerBeanMapper().map(d, Datasource.class);

        datasourceDao.save(d);
        expected.setId(d.getId());

        new DbTableAsserterBuilder(Datasource.TABLE, datasourceTable(expected)).build().assertTable();
    }

    @Test
    public void testUniqueLabel() {
        Datasource d = datasource();
        datasourceDbSetup(d);

        Datasource newD = datasourceWithoutId();
        newD.setLabel(d.getLabel());
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
