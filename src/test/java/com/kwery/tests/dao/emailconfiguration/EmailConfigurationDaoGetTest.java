package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.CompositeOperation;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class EmailConfigurationDaoGetTest extends RepoDashDaoTestBase {
    protected Map<Integer, EmailConfiguration> idEmailConfiguration = new HashMap<>(2);

    protected EmailConfigurationDao emailConfigurationDao;

    @Before
    public void setUpEmailConfigurationDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            EmailConfiguration e = new EmailConfiguration();
            e.setId(i);
            e.setBcc("foo@goo.com");
            e.setReplyTo("bar@moo.com");

            idEmailConfiguration.put(i, e);

            new DbSetup(
                    new DataSourceDestination(DbUtil.getDatasource()),
                    CompositeOperation.sequenceOf(
                            Operations.insertInto(
                                    TABLE_EMAIL_CONFIGURATION
                            ).row()
                                    .column(COLUMN_ID, e.getId())
                                    .column(COLUMN_BCC, e.getBcc())
                                    .column(COLUMN_REPLY_TO, e.getReplyTo())
                                    .end()
                                    .build()
                    )
            ).launch();
        }

        emailConfigurationDao = getInstance(EmailConfigurationDao.class);
    }

    @Test
    public void testGetAll() {
        List<EmailConfiguration> es = emailConfigurationDao.get();

        for (EmailConfiguration e : es) {
            assertThat(e, theSameBeanAs(idEmailConfiguration.get(e.getId())));
        }
    }

    @Test
    public void testGetById() {
        assertThat(emailConfigurationDao.get(1), theSameBeanAs(idEmailConfiguration.get(1)));
    }
}
