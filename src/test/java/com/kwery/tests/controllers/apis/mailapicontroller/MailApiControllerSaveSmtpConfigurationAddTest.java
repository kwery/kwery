package com.kwery.tests.controllers.apis.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpDetail;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.util.TestUtil;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ADDED_M;
import static com.kwery.views.ActionResult.Status.success;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveSmtpConfigurationAddTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpDetail detail = new SmtpDetail();
        detail.setHost("foo.com");
        detail.setPort(446);
        detail.setSsl(true);
        detail.setUsername("username");
        detail.setPassword("password");

        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveSmtpConfiguration");
        String response = ninjaTestBrowser.postJson(getUrl(url), detail);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(success.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(SMTP_CONFIGURATION_ADDED_M)));

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpDetail.TABLE_SMTP_DETAILS)
                .with(SmtpDetail.COLUMN_ID, TestUtil.DB_START_ID)
                .with(SmtpDetail.COLUMN_HOST, detail.getHost())
                .with(SmtpDetail.COLUMN_PORT, detail.getPort())
                .with(SmtpDetail.COLUMN_SSL, detail.isSsl())
                .with(SmtpDetail.COLUMN_USERNAME, detail.getUsername())
                .with(SmtpDetail.COLUMN_PASSWORD, detail.getPassword())
                .add();

        assertDbState(SmtpDetail.TABLE_SMTP_DETAILS, builder.build());
    }
}
