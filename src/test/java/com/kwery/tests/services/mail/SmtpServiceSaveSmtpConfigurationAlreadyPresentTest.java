package com.kwery.tests.services.mail;

import com.kwery.models.SmtpDetail;
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
    protected SmtpDetail smtpDetail;

    @Before
    public void setUpSmtpServiceSaveSmtpConfigurationAlreadyPresentTest() {
        smtpDetail = new SmtpDetail();
        smtpDetail.setId(1);
        smtpDetail.setHost("foo.com");
        smtpDetail.setPort(465);
        smtpDetail.setSsl(true);
        smtpDetail.setUsername("username");
        smtpDetail.setPassword("password");

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SmtpDetail.TABLE_SMTP_DETAILS)
                                .row()
                                .column(SmtpDetail.COLUMN_ID, smtpDetail.getId())
                                .column(SmtpDetail.COLUMN_HOST, smtpDetail.getHost())
                                .column(SmtpDetail.COLUMN_PORT, smtpDetail.getPort())
                                .column(SmtpDetail.COLUMN_SSL, smtpDetail.isSsl())
                                .column(SmtpDetail.COLUMN_USERNAME, smtpDetail.getUsername())
                                .column(SmtpDetail.COLUMN_PASSWORD, smtpDetail.getPassword())
                                .end()
                                .build()
                )
        );

        dbSetup.launch();

        smtpService = getInstance(SmtpService.class);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpDetail.setId(null);
        smtpService.save(smtpDetail);
    }

    @Test(expected = SmtpConfigurationAlreadyPresentException.class)
    public void testNonNullId() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        smtpDetail.setId(2);
        smtpService.save(smtpDetail);
    }
}
