package com.kwery.tests.dao.urlsettingdao;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.UrlSetting;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;

public class UrlSettingDaoSaveTest extends RepoDashDaoTestBase {
    DomainSettingDao domainSettingDao;

    @Before
    public void setUp() {
        domainSettingDao = getInstance(DomainSettingDao.class);
    }

    @Test
    public void testSuccess() throws Exception {
        UrlSetting urlSetting = domainSettingWithoutId();

        DozerBeanMapper mapper = new DozerBeanMapper();
        UrlSetting expected = mapper.map(urlSetting, UrlSetting.class);

        domainSettingDao.save(urlSetting);

        expected.setId(urlSetting.getId());

        new DbTableAsserterBuilder(UrlSetting.URL_SETTING_TABLE, domainSettingTable(expected)).build().assertTable();

    }

    @Test(expected = PersistenceException.class)
    public void testConstraints() {
        UrlSetting urlSetting = new UrlSetting();
        domainSettingDao.save(urlSetting);
    }
}
