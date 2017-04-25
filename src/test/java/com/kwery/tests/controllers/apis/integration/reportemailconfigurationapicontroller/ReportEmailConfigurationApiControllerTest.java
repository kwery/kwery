package com.kwery.tests.controllers.apis.integration.reportemailconfigurationapicontroller;

import com.kwery.controllers.apis.ReportEmailConfigurationApiController;
import com.kwery.models.ReportEmailConfigurationModel;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static com.kwery.models.ReportEmailConfigurationModel.REPORT_EMAIL_CONFIGURATION_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.reportEmailConfigurationDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.reportEmailConfigurationTable;
import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ReportEmailConfigurationApiControllerTest extends AbstractPostLoginApiTest {
    @Test
    public void testGet() {
        ReportEmailConfigurationModel m = reportEmailConfigurationModel();
        reportEmailConfigurationDbSetUp(m);
        String url = getInjector().getInstance(Router.class).getReverseRoute(ReportEmailConfigurationApiController.class, "getReportEmailConfiguration");
        String response = ninjaTestBrowser.makeJsonRequest(getUrl(url));

        assertThat(response, isJson(
                allOf(
                        withJsonPath("$.id", equalTo(m.getId())),
                        withJsonPath("$.logoUrl", equalTo(m.getLogoUrl()))
                )
        ));
    }

    @Test
    public void testSave() throws DatabaseUnitException, SQLException, IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(ReportEmailConfigurationApiController.class, "saveReportEmailConfiguration");
        ReportEmailConfigurationModel m = reportEmailConfigurationModelWithoutId();
        String response = ninjaTestBrowser.postJson(getUrl(url), m);
        assertSuccessActionResultStatus(response);
        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, reportEmailConfigurationTable(m)).columnsToIgnore("id", "created", "updated").build().assertTable();
    }

    @Test
    public void testUpdate() throws DatabaseUnitException, SQLException, IOException {
        String url = getInjector().getInstance(Router.class).getReverseRoute(ReportEmailConfigurationApiController.class, "saveReportEmailConfiguration");
        ReportEmailConfigurationModel m = reportEmailConfigurationModel();
        reportEmailConfigurationDbSetUp(m);

        ReportEmailConfigurationModel updated = reportEmailConfigurationModelWithoutId();
        updated.setId(m.getId());

        String response = ninjaTestBrowser.postJson(getUrl(url), updated);
        assertSuccessActionResultStatus(response);
        new DbTableAsserterBuilder(REPORT_EMAIL_CONFIGURATION_TABLE, reportEmailConfigurationTable(updated)).columnsToIgnore("id", "created", "updated").build().assertTable();
    }
}
