package com.kwery.tests.dao.kweryversiondao;

import com.kwery.dao.KweryVersionDao;
import com.kwery.models.KweryVersionModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.kweryVersionDbSetup;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class KweryVersionDaoGetTest extends RepoDashDaoTestBase {
    protected KweryVersionDao kweryVersionDao;
    private KweryVersionModel kweryVersionModel;

    @Before
    public void setUp() {
        kweryVersionModel = new KweryVersionModel();
        kweryVersionModel.setVersion("foo bar moo");
        kweryVersionModel.setId(DbUtil.dbId());
        kweryVersionDbSetup(kweryVersionModel);
        kweryVersionDao = getInstance(KweryVersionDao.class);
    }

    @Test
    public void test() {
        KweryVersionModel fromDb = kweryVersionDao.get();
        assertThat(fromDb, theSameBeanAs(kweryVersionModel));
    }
}
