package com.kwery.tests.dao.urlconfigurationdao;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.UrlConfiguration.URL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationTable;
import static com.kwery.tests.util.TestUtil.domainSetting;

public class UrlConfigurationDaoUpdateTest extends RepoDashDaoTestBase {
    private UrlConfiguration urlConfiguration;
    private DomainConfigurationDao domainConfigurationDao;

    @Before
    public void setUp() {
        urlConfiguration = domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);
        domainConfigurationDao = getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void test() throws Exception {
        UrlConfiguration updated = domainSetting();
        updated.setId(urlConfiguration.getId());

        DozerBeanMapper mapper = new DozerBeanMapper();
        UrlConfiguration expected = mapper.map(updated, UrlConfiguration.class);

        domainConfigurationDao.save(updated);

        new DbTableAsserterBuilder(URL_CONFIGURATION_TABLE, domainConfigurationTable(expected)).build().assertTable();
    }
}
