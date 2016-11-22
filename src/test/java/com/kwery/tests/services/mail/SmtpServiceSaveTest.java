package com.kwery.tests.services.mail;

import com.kwery.models.SmtpDetail;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.SmtpService;
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
        SmtpDetail details = new SmtpDetail();
        details.setHost("foo.com");
        details.setPort(465);
        details.setSsl(true);
        details.setUsername("username");
        details.setPassword("password");

        getInstance(SmtpService.class).save(details);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpDetail.TABLE_SMTP_DETAILS)
                .with(SmtpDetail.COLUMN_HOST, details.getHost())
                .with(SmtpDetail.COLUMN_PORT, details.getPort())
                .with(SmtpDetail.COLUMN_SSL, details.isSsl())
                .with(SmtpDetail.COLUMN_USERNAME, details.getUsername())
                .with(SmtpDetail.COLUMN_PASSWORD, details.getPassword())
                .add();

        assertDbState(SmtpDetail.TABLE_SMTP_DETAILS, builder.build(), SmtpDetail.COLUMN_ID);
    }
}
