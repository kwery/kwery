package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class EmailConfigurationDaoGetTest extends RepoDashDaoTestBase {
    protected EmailConfigurationDao emailConfigurationDao;
    private EmailConfiguration emailConfiguration;

    @Before
    public void setUpEmailConfigurationDaoGetTest() {
        emailConfiguration = emailConfiguration();
        emailConfigurationDbSet(emailConfiguration);
        emailConfigurationDao = getInstance(EmailConfigurationDao.class);
    }

    @Test
    public void testGet() {
        assertThat(emailConfigurationDao.get(), theSameBeanAs(emailConfiguration));
    }

    @Test
    public void testGetById() {
        assertThat(emailConfigurationDao.get(emailConfiguration.getId()), theSameBeanAs(emailConfiguration));
    }
}
