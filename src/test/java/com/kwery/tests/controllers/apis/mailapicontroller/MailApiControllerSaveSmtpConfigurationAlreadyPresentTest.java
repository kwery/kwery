package com.kwery.tests.controllers.apis.mailapicontroller;

import com.kwery.controllers.apis.MailApiController;
import com.kwery.models.SmtpDetail;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import ninja.Router;
import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.kwery.tests.fluentlenium.utils.DbUtil.assertDbState;
import static com.kwery.tests.util.Messages.SMTP_CONFIGURATION_ALREADY_PRESENT_M;
import static com.kwery.views.ActionResult.Status.failure;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MailApiControllerSaveSmtpConfigurationAlreadyPresentTest extends AbstractPostLoginApiTest {
    protected SmtpDetail smtpDetail;

    @Before
    public void setUpMailApiControllerSaveSmtpConfigurationAlreadyPresentTest() {
        smtpDetail = new SmtpDetail();
        smtpDetail.setId(1);
        smtpDetail.setHost("foo.com");
        smtpDetail.setPort(465);
        smtpDetail.setSsl(true);
        smtpDetail.setUsername("username");
        smtpDetail.setPassword("password");

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SmtpDetail.TABLE_SMTP_DETAILS)
                                .row()
                                .column(SmtpDetail.COLUMN_ID, smtpDetail.getId())
                                .column(SmtpDetail.COLUMN_HOST, smtpDetail.getHost())
                                .column(SmtpDetail.COLUMN_PORT, smtpDetail.getPort())
                                .column(SmtpDetail.COLUMN_SSL, smtpDetail.isSsl())
                                .column(SmtpDetail.COLUMN_USERNAME, smtpDetail.getUsername())
                                .column(SmtpDetail.COLUMN_PASSWORD, smtpDetail.getPassword())
                                .end()
                                .build()
                )
        );

        dbSetup.launch();
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        SmtpDetail newDetail = new SmtpDetail();
        newDetail.setHost("bar.com");
        newDetail.setPort(465);
        newDetail.setSsl(true);
        newDetail.setUsername("username");
        newDetail.setPassword("password");

        String url = getInjector().getInstance(Router.class).getReverseRoute(MailApiController.class, "saveSmtpConfiguration");
        String response = ninjaTestBrowser.postJson(getUrl(url), newDetail);

        assertThat(response, isJson());
        assertThat(response, hasJsonPath("$.status", is(failure.name())));
        assertThat(response, hasJsonPath("$.messages[0]", is(SMTP_CONFIGURATION_ALREADY_PRESENT_M)));

        DataSetBuilder builder = new DataSetBuilder();

        builder.newRow(SmtpDetail.TABLE_SMTP_DETAILS)
                .with(SmtpDetail.COLUMN_ID, smtpDetail.getId())
                .with(SmtpDetail.COLUMN_HOST, smtpDetail.getHost())
                .with(SmtpDetail.COLUMN_PORT, smtpDetail.getPort())
                .with(SmtpDetail.COLUMN_SSL, smtpDetail.isSsl())
                .with(SmtpDetail.COLUMN_USERNAME, smtpDetail.getUsername())
                .with(SmtpDetail.COLUMN_PASSWORD, smtpDetail.getPassword())
                .add();

        assertDbState(SmtpDetail.TABLE_SMTP_DETAILS, builder.build());
    }
}
