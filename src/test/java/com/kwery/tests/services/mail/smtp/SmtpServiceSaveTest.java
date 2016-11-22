package com.kwery.tests.services.mail.smtp;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.smtp.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.smtp.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.smtp.SmtpService;
import com.kwery.tests.util.RepoDashTestBase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;

public class SmtpServiceSaveTest extends RepoDashTestBase {
    @Test
    public void test() throws DatabaseUnitException, SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException, SQLException, IOException {
        SmtpConfiguration details = new SmtpConfiguration();
        details.setHost("foo.com");
        details.setPort(465);
        details.setSsl(true);
        details.setUsername("username");
        details.setPassword("password");

        getInstance(SmtpService.class).save(details);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                .with(SmtpConfiguration.COLUMN_HOST, details.getHost())
                .with(SmtpConfiguration.COLUMN_PORT, details.getPort())
                .with(SmtpConfiguration.COLUMN_SSL, details.isSsl())
                .with(SmtpConfiguration.COLUMN_USERNAME, details.getUsername())
                .with(SmtpConfiguration.COLUMN_PASSWORD, details.getPassword())
                .add();

        assertDbState(SmtpConfiguration.TABLE_SMTP_CONFIGURATION, builder.build(), SmtpConfiguration.COLUMN_ID);
    }
}
