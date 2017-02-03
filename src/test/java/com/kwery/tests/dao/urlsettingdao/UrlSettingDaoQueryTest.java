package com.kwery.tests.dao.urlsettingdao;

import com.kwery.dao.DomainSettingDao;
import com.kwery.models.UrlSetting;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.domainSettingDbSetUp;
import static com.kwery.tests.util.TestUtil.domainSetting;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class UrlSettingDaoQueryTest extends RepoDashDaoTestBase {
    protected DomainSettingDao domainSettingDao;
    private UrlSetting urlSetting;

    @Before
    public void setUp() {
        urlSetting = domainSetting();
        domainSettingDbSetUp(urlSetting);
        domainSettingDao = getInstance(DomainSettingDao.class);
    }

    @Test
    public void test() {
        List<UrlSetting> settings = domainSettingDao.get();
        assertThat(settings, hasSize(1));
        assertThat(settings.get(0), theSameBeanAs(urlSetting));
    }
}
