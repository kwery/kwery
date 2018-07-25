package com.kwery.tests.fluentlenium.reportemailconfiguration;

import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import org.dbunit.DatabaseUnitException;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.ReportEmailConfigurationModel.REPORT_EMAIL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.reportEmailConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.reportEmailConfigurationTable;
import static com.kwery.tests.util.Messages.REPORT_EMAIL_CONFIGURATION_SAVE_SUCCESS;
import static com.kwery.tests.util.TestUtil.reportEmailConfigurationModel;
import static junit.framework.TestCase.fail;

public class ReportEmailConfigurationUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Page
    protected ReportEmailConfigurationPage page;
    private ReportEmailConfigurationModel reportEmailConfigurationModel;

    @Before
    public void setUp() {
        reportEmailConfigurationModel = reportEmailConfigurationModel();
        reportEmailConfigurationDbSetUp(reportEmailConfigurationModel);

        page.go();
        if (!page.isRendered()) {
            fail("Could not render report email configuration save page");
        }
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        page.submitForm("https://www.google.co.in/logos/doodles/2017/rajkumars-88th-birthday-5129031727972352-l.png");
        page.getActionResultComponent().assertSuccessMessage(REPORT_EMAIL_CONFIGURATION_SAVE_SUCCESS);

        ReportEmailConfigurationModel m = new ReportEmailConfigurationModel();
        m.setLogoUrl("https://www.google.co.in/logos/doodles/2017/rajkumars-88th-birthday-5129031727972352-l.png");
        m.setId(reportEmailConfigurationModel.getId());

        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, reportEmailConfigurationTable(m))
                .columnsToIgnore("created", "updated").build().assertTable();
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}

