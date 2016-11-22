package com.kwery.tests.services.mail;

import com.kwery.models.SmtpDetail;
import com.kwery.services.mail.MultipleSmtpConfigurationFoundException;
import com.kwery.services.mail.SmtpConfigurationAlreadyPresentException;
import com.kwery.services.mail.SmtpService;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.ninja_squad.dbsetup.Operations.insertInto;

public class SmtpServiceSaveUpdateTest extends RepoDashTestBase {
    protected SmtpService smtpService;

    protected int smtpDetailId = 1;

    @Before
    public void setUpSmtpServiceSaveUpdateTest () {
        SmtpDetail smtpDetail = new SmtpDetail();
        smtpDetail.setId(smtpDetailId);
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

    @Test
    public void test() throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException, DatabaseUnitException, SQLException, IOException {
        SmtpDetail detail = new SmtpDetail();
        detail.setId(smtpDetailId);
        detail.setHost("bar.com");
        detail.setPort(466);
        detail.setSsl(false);
        detail.setUsername("name");
        detail.setPassword("pass");

        smtpService.save(detail);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpDetail.TABLE_SMTP_DETAILS)
                .with(SmtpDetail.COLUMN_ID, detail.getId())
                .with(SmtpDetail.COLUMN_HOST, detail.getHost())
                .with(SmtpDetail.COLUMN_PORT, detail.getPort())
                .with(SmtpDetail.COLUMN_SSL, detail.isSsl())
                .with(SmtpDetail.COLUMN_USERNAME, detail.getUsername())
                .with(SmtpDetail.COLUMN_PASSWORD, detail.getPassword())
                .add();

        assertDbState(SmtpDetail.TABLE_SMTP_DETAILS, builder.build());
    }
}
