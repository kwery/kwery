package com.kwery.tests.dao.kweryversiondao;

import com.kwery.dao.KweryVersionDao;
import com.kwery.models.KweryVersionModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.KweryVersionModel.ID_COLUMN;
import static com.kwery.models.KweryVersionModel.KWERY_VERSION_MODEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.kweryVersionTable;

public class KweryVersionDaoSaveTest extends RepoDashDaoTestBase {
    private KweryVersionDao kweryVersionDao;

    @Before
    public void setUp() {
        kweryVersionDao = getInstance(KweryVersionDao.class);
    }

    @Test
    public void testSuccess() throws Exception {
        KweryVersionModel kweryVersionModel = new KweryVersionModel();
        String version = "foo bar moo";
        kweryVersionModel.setVersion(version);

        kweryVersionDao.save(kweryVersionModel);

        new DbTableAsserterBuilder(KWERY_VERSION_MODEL_TABLE, kweryVersionTable(kweryVersionModel)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultipleSave() {
        KweryVersionModel kweryVersionModel = new KweryVersionModel();
        String version = "foo bar moo";
        kweryVersionModel.setVersion(version);
        kweryVersionDao.save(kweryVersionModel);

        kweryVersionModel = new KweryVersionModel();
        kweryVersionModel.setVersion(version);
        kweryVersionDao.save(kweryVersionModel);
    }
}
