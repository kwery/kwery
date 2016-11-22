package com.kwery.tests.services.mail;

import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.EmailConfigurationExistsException;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.MultipleEmailConfigurationException;
import com.kwery.tests.util.RepoDashTestBase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;

public class EmailConfigurationServiceSaveTest extends RepoDashTestBase {
    @Test
    public void test() throws EmailConfigurationExistsException, MultipleEmailConfigurationException, DatabaseUnitException, SQLException, IOException {
        EmailConfiguration e = new EmailConfiguration();
        e.setBcc("foo@goo.com");
        e.setReplyTo("bar@cho.com");

        getInstance(EmailConfigurationService.class).save(e);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_ID, e.getId())
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, builder.build());
    }
}
