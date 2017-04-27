package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.services.mail.InvalidEmailException;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationTable;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;

public class EmailConfigurationDaoSaveNewTest extends RepoDashDaoTestBase {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException, InvalidEmailException {
        EmailConfiguration e = emailConfigurationWithoutId();
        getInstance(EmailConfigurationDao.class).save(e);
        new DbTableAsserterBuilder(TABLE_EMAIL_CONFIGURATION, emailConfigurationTable(e)).build().assertTable();
     }
}
