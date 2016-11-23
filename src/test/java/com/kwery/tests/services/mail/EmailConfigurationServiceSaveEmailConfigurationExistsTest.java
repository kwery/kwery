package com.kwery.tests.services.mail;

import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.EmailConfigurationExistsException;
import com.kwery.services.mail.EmailConfigurationService;
import com.kwery.services.mail.MultipleEmailConfigurationException;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_ID;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class EmailConfigurationServiceSaveEmailConfigurationExistsTest extends RepoDashTestBase {
    protected EmailConfigurationService emailConfigurationService;

    protected EmailConfiguration e;

    @Before
    public void setUpEmailConfigurationServiceSaveEmailConfigurationExistsTest() {
        e = new EmailConfiguration();
        e.setId(1);
        e.setFrom("from@foo.com");
        e.setReplyTo("foo@bar.com");
        e.setBcc("bar@foo.com");

        new DbSetup(new DataSourceDestination(
                getDatasource()),
                sequenceOf(
                        insertInto(TABLE_EMAIL_CONFIGURATION)
                                .row()
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

    @Test(expected = EmailConfigurationExistsException.class)
    public void test() throws MultipleEmailConfigurationException, EmailConfigurationExistsException, DatabaseUnitException, SQLException, IOException {
        e.setId(null);
        emailConfigurationService.save(e);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_ID, e.getId())
                .with(COLUMN_FROM_EMAIL, e.getFrom())
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, builder.build());
    }
}
