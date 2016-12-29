package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.List;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.Messages.UPDATE_M;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DatasourceUpdateUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = RuleChain.outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    protected UpdateDatasourcePage page;
    protected Datasource datasource;

    @Before
    public void setUpUpdateDatasourcePageTest() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, datasource.getLabel(), datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .values(2, "foo", datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();


        page = createPage(UpdateDatasourcePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render update datasource page");
        }
    }

    @Test
    public void test() {
        page.waitForForm("label", "testDatasource0");

        List<String> fields = page.formFields();

        assertThat(fields, hasSize(5));

        assertThat(fields.get(0), is(datasource.getUrl()));
        assertThat(fields.get(1), is(String.valueOf(datasource.getPort())));
        assertThat(fields.get(2), is(datasource.getUsername()));
        assertThat(fields.get(3), is(datasource.getPassword()));
        assertThat(fields.get(4), is(datasource.getLabel()));

        assertThat(page.actionLabel().toLowerCase(), is(UPDATE_M.toLowerCase()));

        String newLabel = "newLabel";
        page.fillLabel(newLabel);
        page.submit();

        page.waitForSuccessMessage(newLabel);

        page.fillLabel("foo");
        page.submit();

        page.waitForFailureMessage("foo", datasource.getType());
    }
}
