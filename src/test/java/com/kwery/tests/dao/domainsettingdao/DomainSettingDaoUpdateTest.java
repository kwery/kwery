package com.kwery.tests.dao.domainsettingdao;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.DomainSetting;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.DomainSetting.DOMAIN_SETTING_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSetting;

public class DomainSettingDaoUpdateTest extends RepoDashDaoTestBase {
    private DomainSetting domainSetting;
    private DomainSettingDao domainSettingDao;

    @Before
    public void setUp() {
        domainSetting = domainSetting();
        domainSettingDbSetUp(domainSetting);
        domainSettingDao = getInstance(DomainSettingDao.class);
    }

    @Test
    public void test() throws Exception {
        DomainSetting updated = domainSetting();
        updated.setId(domainSetting.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        DomainSetting expected = mapper.map(updated, DomainSetting.class);

        domainSettingDao.save(updated);

        new DbTableAsserterBuilder(DOMAIN_SETTING_TABLE, domainSettingTable(expected)).build().assertTable();
    }
}
