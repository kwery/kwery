package com.kwery.tests.dao.reportemailconfigurationdao;

import com.kwery.dao.ReportEmailConfigurationDao;
import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.ReportEmailConfigurationModel.REPORT_EMAIL_CONFIGURATION_TABLE;

public class ReportEmailConfigurationDaoUpdateTest extends RepoDashDaoTestBase {
    private ReportEmailConfigurationDao dao;
    private ReportEmailConfigurationModel m;

    @Before
    public void setUp() {
        dao = getInstance(ReportEmailConfigurationDao.class);
        m = TestUtil.reportEmailConfigurationModel();
        DbUtil.reportEmailConfigurationDbSetUp(m);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        ReportEmailConfigurationModel updated = TestUtil.reportEmailConfigurationModelWithoutId();
        updated.setId(m.getId());
        dao.save(updated);
        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, DbUtil.reportEmailConfigurationTable(updated)).build().assertTable();
    }
}
