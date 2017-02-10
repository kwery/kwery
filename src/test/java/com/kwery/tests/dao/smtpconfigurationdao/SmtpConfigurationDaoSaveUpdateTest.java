package com.kwery.tests.dao.smtpconfigurationdao;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.smtpConfiguration;
import static com.kwery.tests.util.TestUtil.smtpConfigurationWithoutId;

public class SmtpConfigurationDaoSaveUpdateTest extends RepoDashDaoTestBase {
    protected SmtpConfigurationDao smtpConfigurationDao;
    private SmtpConfiguration smtpConfiguration;

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        smtpConfiguration = smtpConfiguration();
        smtpConfigurationDbSetUp(smtpConfiguration);
        smtpConfigurationDao = getInstance(SmtpConfigurationDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpConfiguration detail = smtpConfigurationWithoutId();
        detail.setId(smtpConfiguration.getId());
        smtpConfigurationDao.save(detail);
        assertDbState(TABLE_SMTP_CONFIGURATION, smtpConfigurationTable(detail));
    }
}
