package com.kwery.tests.services.mail;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.SmtpConfigurationNotFoundException;
import com.kwery.services.mail.SmtpService;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SmtpServiceGetSmtpConfigurationMultipleConfigurationTest extends RepoDashTestBase {
    @Before
    public void setUpSmtpDetailDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
            smtpConfiguration.setId(i);
            smtpConfiguration.setHost("foo.com");
            smtpConfiguration.setPort(465);
            smtpConfiguration.setSsl(true);
            smtpConfiguration.setUsername("username");
            smtpConfiguration.setPassword("password");

            DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                    Operations.sequenceOf(
                            insertInto(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                                    .row()
                                    .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                    .column(SmtpConfiguration.COLUMN_HOST, smtpConfiguration.getHost())
                                    .column(SmtpConfiguration.COLUMN_PORT, smtpConfiguration.getPort())
                                    .column(SmtpConfiguration.COLUMN_SSL, smtpConfiguration.isSsl())
                                    .column(SmtpConfiguration.COLUMN_USERNAME, smtpConfiguration.getUsername())
                                    .column(SmtpConfiguration.COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                    .end()
                                    .build()
                    )
            );

            dbSetup.launch();
        }
    }

    @Test(expected = MultipleSmtpConfigurationFoundException.class)
    public void test() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        getInstance(SmtpService.class).getSmtpConfiguration();
    }
}
