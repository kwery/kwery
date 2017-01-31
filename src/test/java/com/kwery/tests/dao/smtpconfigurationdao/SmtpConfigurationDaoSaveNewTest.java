package com.kwery.tests.dao.smtpconfigurationdao;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.SmtpConfiguration.COLUMN_ID;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationTable;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmtpConfigurationDaoSaveNewTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpConfiguration details = smtpConfigurationWithoutId();
        getInstance(SmtpConfigurationDao.class).save(details);
        assertDbState(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(details), COLUMN_ID);
    }
}
