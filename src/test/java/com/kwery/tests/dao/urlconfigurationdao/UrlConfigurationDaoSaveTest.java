package com.kwery.tests.dao.urlconfigurationdao;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationTable;
import static com.kwery.tests.util.TestUtil.domainSettingWithoutId;

public class UrlConfigurationDaoSaveTest extends RepoDashDaoTestBase {
    DomainConfigurationDao domainConfigurationDao;

    @Before
    public void setUp() {
        domainConfigurationDao = getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void testSuccess() throws Exception {
        UrlConfiguration urlConfiguration = domainSettingWithoutId();

        DozerBeanMapper mapper = new DozerBeanMapper();
        UrlConfiguration expected = mapper.map(urlConfiguration, UrlConfiguration.class);

        domainConfigurationDao.save(urlConfiguration);

        expected.setId(urlConfiguration.getId());

        new DbTableAsserterBuilder(UrlConfiguration.URL_CONFIGURATION_TABLE, domainConfigurationTable(expected)).build().assertTable();

    }

    @Test(expected = PersistenceException.class)
    public void testConstraints() {
        UrlConfiguration urlConfiguration = new UrlConfiguration();
        domainConfigurationDao.save(urlConfiguration);
    }
}
