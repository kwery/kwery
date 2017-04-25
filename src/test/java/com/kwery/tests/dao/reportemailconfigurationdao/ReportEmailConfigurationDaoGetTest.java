package com.kwery.tests.dao.reportemailconfigurationdao;

import com.kwery.dao.ReportEmailConfigurationDao;
import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.reportEmailConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.reportEmailConfigurationModel;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class ReportEmailConfigurationDaoGetTest extends RepoDashDaoTestBase {
    private ReportEmailConfigurationDao reportEmailConfigurationDao;

    @Before
    public void setUp() {
        reportEmailConfigurationDao = getInstance(ReportEmailConfigurationDao.class);
    }

    @Test
    public void testGetOne() {
        reportEmailConfigurationDbSetUp(reportEmailConfigurationModel());
        ReportEmailConfigurationModel m = reportEmailConfigurationDao.get();
        assertThat(m, notNullValue());
    }

    @Test(expected = RuntimeException.class)
    public void testMultiple() {
        reportEmailConfigurationDbSetUp(reportEmailConfigurationModel());
        reportEmailConfigurationDbSetUp(reportEmailConfigurationModel());
        reportEmailConfigurationDao.get();
    }

    @Test
    public void testNull() {
        ReportEmailConfigurationModel m = reportEmailConfigurationDao.get();
        assertThat(m, nullValue());
    }
}
