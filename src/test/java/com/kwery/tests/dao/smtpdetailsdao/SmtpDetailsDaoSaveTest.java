package com.kwery.tests.dao.smtpdetailsdao;

import com.kwery.dao.SmtpDetailsDao;
import com.kwery.models.SmtpDetails;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;

public class SmtpDetailsDaoSaveTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpDetails details = new SmtpDetails();
        details.setHost("foo.com");
        details.setPort(1);
        details.setSsl(true);
        details.setUsername("username");
        details.setPassword("password");

        getInstance(SmtpDetailsDao.class).save(details);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpDetails.TABLE_SMTP_DETAILS)
                .with(SmtpDetails.COLUMN_HOST, details.getHost())
                .with(SmtpDetails.COLUMN_PORT, details.getPort())
                .with(SmtpDetails.COLUMN_SSL, details.isSsl())
                .with(SmtpDetails.COLUMN_USERNAME, details.getUsername())
                .with(SmtpDetails.COLUMN_PASSWORD, details.getPassword())
                .add();

        assertDbState(SmtpDetails.TABLE_SMTP_DETAILS, builder.build(), SmtpDetails.COLUMN_ID);
    }
}
