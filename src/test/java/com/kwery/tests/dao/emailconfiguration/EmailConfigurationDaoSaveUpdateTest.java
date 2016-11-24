package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
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

public class EmailConfigurationDaoSaveUpdateTest extends RepoDashDaoTestBase {
    protected EmailConfigurationDao emailConfigurationDao;
    protected EmailConfiguration e;

    @Before
    public void setUpEmailConfigurationDaoSaveUpdateTest() {
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

        emailConfigurationDao = getInstance(EmailConfigurationDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        e.setBcc("goo@moo.com");
        e.setReplyTo("foo@cho.com");

        emailConfigurationDao.save(e);

        DataSetBuilder b = new DataSetBuilder();
        b.newRow(TABLE_EMAIL_CONFIGURATION)
                .with(COLUMN_ID, e.getId())
                .with(COLUMN_FROM_EMAIL, e.getFrom())
                .with(COLUMN_BCC, e.getBcc())
                .with(COLUMN_REPLY_TO, e.getReplyTo())
                .add();

        assertDbState(TABLE_EMAIL_CONFIGURATION, b.build());
    }
}
