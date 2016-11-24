package com.kwery.tests.services.mail;

import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.MultipleEmailConfigurationException;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.CompositeOperation;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class EmailConfigurationServiceGetEmailConfigurationTest extends RepoDashTestBase {
    protected EmailConfigurationService emailConfigurationService;

    protected EmailConfiguration e;

    @Before
    public void setUpEmailConfigurationServiceGetEmailConfigurationTest() {
        e = new EmailConfiguration();
        e.setId(1);
        e.setFrom("from@foo.com");
        e.setBcc("foo@goo.com");
        e.setReplyTo("bar@moo.com");

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                CompositeOperation.sequenceOf(
                        Operations.insertInto(
                                TABLE_EMAIL_CONFIGURATION
                        ).row()
                                .column(COLUMN_ID, e.getId())
                                .column(COLUMN_FROM_EMAIL, e.getFrom())
                                .column(COLUMN_BCC, e.getBcc())
                                .column(COLUMN_REPLY_TO, e.getReplyTo())
                                .end()
                                .build()
                )
        ).launch();

        emailConfigurationService = getInstance(EmailConfigurationService.class);
    }

    @Test
    public void test() throws MultipleEmailConfigurationException {
        assertThat(emailConfigurationService.getEmailConfiguration(), theSameBeanAs(e));
    }
}
