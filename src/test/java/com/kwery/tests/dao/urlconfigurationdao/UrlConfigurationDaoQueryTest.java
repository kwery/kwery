package com.kwery.tests.dao.urlconfigurationdao;

import com.kwery.dao.DomainConfigurationDao;
import com.kwery.models.UrlConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class UrlConfigurationDaoQueryTest extends RepoDashDaoTestBase {
    protected DomainConfigurationDao domainConfigurationDao;
    private UrlConfiguration urlConfiguration;

    @Before
    public void setUp() {
        urlConfiguration = domainSetting();
        domainConfigurationDbSetUp(urlConfiguration);
        domainConfigurationDao = getInstance(DomainConfigurationDao.class);
    }

    @Test
    public void test() {
        List<UrlConfiguration> settings = domainConfigurationDao.get();
        assertThat(settings, hasSize(1));
        assertThat(settings.get(0), theSameBeanAs(urlConfiguration));
    }
}
