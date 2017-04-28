package com.kwery.tests.dao.reportemailconfigurationdao;

import com.kwery.dao.ReportEmailConfigurationDao;
import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import com.mysql.cj.jdbc.ha.ReplicationConnectionGroupManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.ReportEmailConfigurationModel.REPORT_EMAIL_CONFIGURATION_TABLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class ReportEmailConfigurationDaoSaveTest extends RepoDashDaoTestBase {
    private ReportEmailConfigurationDao dao;

    @Before
    public void setUp() {
        dao = getInstance(ReportEmailConfigurationDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        ReportEmailConfigurationModel m = TestUtil.reportEmailConfigurationModelWithoutId();
        TestUtil.nullifyTimestamps(m);

        long now = System.currentTimeMillis();

        dao.save(m);
        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, DbUtil.reportEmailConfigurationTable(m)).build().assertTable();

        assertThat(m.getCreated(), greaterThanOrEqualTo(now));
        assertThat(m.getUpdated(), greaterThanOrEqualTo(now));
    }

    @Test
    public void testNull() throws DatabaseUnitException, SQLException, IOException {
        ReportEmailConfigurationModel m = TestUtil.reportEmailConfigurationModelWithoutId();
        m.setLogoUrl(null);
        TestUtil.nullifyTimestamps(m);
        dao.save(m);
        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, DbUtil.reportEmailConfigurationTable(m)).build().assertTable();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyString() {
        ReportEmailConfigurationModel m = TestUtil.reportEmailConfigurationModelWithoutId();
        m.setLogoUrl("");
        dao.save(m);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidLength() {
        ReportEmailConfigurationModel m = TestUtil.reportEmailConfigurationModelWithoutId();
        m.setLogoUrl(RandomStringUtils.randomAlphanumeric(ReportEmailConfigurationModel.LOGO_URL_MAX + 1));
        dao.save(m);
    }
}
