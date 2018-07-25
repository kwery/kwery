package com.kwery.tests.dao.emailconfiguration;

import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;
import com.kwery.services.EmptyFromEmailException;
import com.kwery.services.mail.InvalidEmailException;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dbunit.DatabaseUnitException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationDbSet;
import static com.kwery.tests.fluentlenium.utils.DbUtil.emailConfigurationTable;
import static com.kwery.tests.util.TestUtil.emailConfiguration;
import static com.kwery.tests.util.TestUtil.emailConfigurationWithoutId;
import static junit.framework.TestCase.fail;

public class EmailConfigurationDaoSaveUpdateTest extends RepoDashDaoTestBase {
    protected EmailConfigurationDao emailConfigurationDao;
    protected EmailConfiguration e;

    @Before
    public void setUpEmailConfigurationDaoSaveUpdateTest() {
        e = emailConfiguration();
        emailConfigurationDbSet(e);
        emailConfigurationDao = getInstance(EmailConfigurationDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException, InvalidEmailException {
        EmailConfiguration updated = TestUtil.emailConfiguration();
        updated.setId(e.getId());
        emailConfigurationDao.save(updated);
        new DbTableAsserterBuilder(TABLE_EMAIL_CONFIGURATION, emailConfigurationTable(updated)).build().assertTable();
    }

    @Test(expected = EmptyFromEmailException.class)
    public void testEmptyFromEmailExceptionTest() {
        EmailConfiguration config = TestUtil.emailConfiguration();
        config.setFrom("            ");
        emailConfigurationDao.save(config);
    }

    @Test
    public void testInvalidEmailException() {
        EmailConfiguration config = TestUtil.emailConfiguration();
        config.setFrom("sdjflkd  ");
        config.setReplyTo("kj lklkjlkjl");
        config.setBcc("abhi@getkwery.com, sdkfdsfkl, foo@bar.com");

        try {
            emailConfigurationDao.save(config);
            fail("Should have thrown InvalidEmailException");
        } catch (InvalidEmailException e) {
            Assert.assertThat(e.getInvalids(), Matchers.containsInAnyOrder("sdjflkd", "kj lklkjlkjl", "sdkfdsfkl"));
        }
    }

    @Test
    public void updateToEmptyTest() throws DatabaseUnitException, SQLException, IOException {
        EmailConfiguration updated = emailConfigurationWithoutId();
        updated.setBcc("");
        updated.setReplyTo("");
        updated.setId(e.getId());
        emailConfigurationDao.save(updated);
        new DbTableAsserterBuilder(TABLE_EMAIL_CONFIGURATION, emailConfigurationTable(updated)).build().assertTable();
    }
}
