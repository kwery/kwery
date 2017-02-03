package com.kwery.tests.dao.urlsettingdao;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.UrlSetting;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.UrlSetting.URL_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSetting;

public class UrlSettingDaoUpdateTest extends RepoDashDaoTestBase {
    private UrlSetting urlSetting;
    private DomainSettingDao domainSettingDao;

    @Before
    public void setUp() {
        urlSetting = domainSetting();
        domainSettingDbSetUp(urlSetting);
        domainSettingDao = getInstance(DomainSettingDao.class);
    }

    @Test
    public void test() throws Exception {
        UrlSetting updated = domainSetting();
        updated.setId(urlSetting.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        UrlSetting expected = mapper.map(updated, UrlSetting.class);

        domainSettingDao.save(updated);

        new DbTableAsserterBuilder(URL_SETTING_TABLE, domainSettingTable(expected)).build().assertTable();
    }
}
