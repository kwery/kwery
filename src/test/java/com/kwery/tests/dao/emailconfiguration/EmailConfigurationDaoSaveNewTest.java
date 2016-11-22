package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
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

public class EmailConfigurationDaoSaveNewTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        EmailConfiguration e = new EmailConfiguration();
        e.setBcc("foo@bar.com");
        e.setReplyTo("bar@foo.com");

        getInstance(EmailConfigurationDao.class).save(e);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, builder.build(), COLUMN_ID);
     }
}
