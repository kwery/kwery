package com.kwery.tests.dao.smtpconfigurationdao;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SmtpConfigurationDaoGetTest extends RepoDashDaoTestBase {
    protected SmtpConfigurationDao smtpConfigurationDao;

    protected Map<Integer, SmtpConfiguration> idDetailMap = new HashMap<>();

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            SmtpConfiguration smtpConfiguration = TestUtil.smtpConfiguration();
            smtpConfiguration.setId(i);
            idDetailMap.put(smtpConfiguration.getId(), smtpConfiguration);
            smtpConfigurationDbSetUp(smtpConfiguration);
        }

        smtpConfigurationDao = getInstance(SmtpConfigurationDao.class);
    }

    @Test
    public void testGet() {
        List<SmtpConfiguration> smtpConfigurationList = smtpConfigurationDao.get();
        assertThat(smtpConfigurationList, hasSize(2));
        assertThat(smtpConfigurationList.get(0), theSameBeanAs(idDetailMap.get(smtpConfigurationList.get(0).getId())));
        assertThat(smtpConfigurationList.get(1), theSameBeanAs(idDetailMap.get(smtpConfigurationList.get(1).getId())));
    }

    @Test
    public void testGetById() {
        assertThat(smtpConfigurationDao.get(1), theSameBeanAs(idDetailMap.get(1)));
        assertThat(smtpConfigurationDao.get(2), theSameBeanAs(idDetailMap.get(2)));
    }
}
