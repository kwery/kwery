package com.kwery.tests.services.mail;

import com.kwery.models.SmtpConfiguration;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.SmtpService;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SmtpServiceSaveSmtpConfigurationAlreadyPresentTest extends RepoDashDaoTestBase {
    protected SmtpService smtpService;
    protected SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpSmtpServiceSaveSmtpConfigurationAlreadyPresentTest() {
        smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setId(1);
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

        smtpService = getInstance(SmtpService.class);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpConfiguration.setId(null);
        smtpService.save(smtpConfiguration);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNonNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpConfiguration.setId(2);
        smtpService.save(smtpConfiguration);
    }
}
