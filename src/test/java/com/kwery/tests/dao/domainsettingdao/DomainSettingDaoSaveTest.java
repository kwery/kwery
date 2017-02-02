package com.kwery.tests.dao.domainsettingdao;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.DomainSetting;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;

public class DomainSettingDaoSaveTest extends RepoDashDaoTestBase {
    DomainSettingDao domainSettingDao;

    @Before
    public void setUp() {
        domainSettingDao = getInstance(DomainSettingDao.class);
    }

    @Test
    public void testSuccess() throws Exception {
        DomainSetting domainSetting = domainSettingWithoutId();

        DozerBeanMapper mapper = new DozerBeanMapper();
        DomainSetting expected = mapper.map(domainSetting, DomainSetting.class);

        domainSettingDao.save(domainSetting);

        expected.setId(domainSetting.getId());

        new DbTableAsserterBuilder(DomainSetting.DOMAIN_SETTING_TABLE, domainSettingTable(expected)).build().assertTable();

    }

    @Test(expected = PersistenceException.class)
    public void testConstraints() {
        DomainSetting domainSetting = new DomainSetting();
        domainSettingDao.save(domainSetting);
    }
}
