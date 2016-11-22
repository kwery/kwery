package com.kwery.tests.dao.smtpconfigurationdao;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
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

public class SmtpConfigurationDaoSaveUpdateTest extends RepoDashDaoTestBase {
    protected SmtpConfigurationDao smtpConfigurationDao;
    protected int smtpDetailId = 1;

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
        smtpConfiguration.setId(smtpDetailId);
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

        smtpConfigurationDao = getInstance(SmtpConfigurationDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpConfiguration detail = new SmtpConfiguration();
        detail.setId(smtpDetailId);
        detail.setHost("bar.com");
        detail.setPort(466);
        detail.setSsl(false);
        detail.setUsername("name");
        detail.setPassword("pass");

        smtpConfigurationDao.save(detail);

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                .with(SmtpConfiguration.COLUMN_ID, detail.getId())
                .with(SmtpConfiguration.COLUMN_HOST, detail.getHost())
                .with(SmtpConfiguration.COLUMN_PORT, detail.getPort())
                .with(SmtpConfiguration.COLUMN_SSL, detail.isSsl())
                .with(SmtpConfiguration.COLUMN_USERNAME, detail.getUsername())
                .with(SmtpConfiguration.COLUMN_PASSWORD, detail.getPassword())
                .add();

        assertDbState(SmtpConfiguration.TABLE_SMTP_CONFIGURATION, builder.build());
    }
}
