package controllers.fluentlenium;

import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;

import static controllers.util.HtmlClass.ISA_ERROR_C;
import static controllers.util.HtmlClass.ISA_INFO_C;
import static controllers.util.HtmlId.CREATE_I;
import static controllers.util.Messages.DATASOURCE_ADDITION_FAILURE_M;
import static controllers.util.Messages.DATASOURCE_ADDITION_SUCCESS_M;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static models.Datasource.Type.MYSQL;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CreateDatasourceFluentLeniumTest extends DashRepoFluentLeniumTest {
    @Test
    public void test() {
        goTo(getServerAddress() + "#onboarding/create-datasource");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(htmlId("username")).isPresent();

        FluentList<FluentWebElement> username = $(htmlNamedTextInputExpression("username"));
        assertTrue("Text input box with name username is present", username.size() > 0);

        FluentList<FluentWebElement> password = $(htmlNamedPasswordInputExpression("password"));
        assertTrue("Text input box with name password is present", password.size() > 0);

        FluentList<FluentWebElement> url = $(htmlNamedTextInputExpression("url"));
        assertTrue("Text input box with name url is present", url.size() > 0);

        FluentList<FluentWebElement> label = $(htmlNamedTextInputExpression("label"));
        assertTrue("Text input box with name label is present", label.size() > 0);

        fill(username).with("purvi");
        fill(password).with("password");
        fill(label).with("test");
        fill(url).with("foo.com");

        click($(htmlId(CREATE_I)));

        String successMessage = format(DATASOURCE_ADDITION_SUCCESS_M, MYSQL, "test");
        String sucMsgExpr = htmlClassExpression(ISA_INFO_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(sucMsgExpr).hasText(successMessage);

        assertThat($(sucMsgExpr).getText(), is(successMessage));

        click($(htmlId(CREATE_I)));
        String datasourceExistsMessage = format(DATASOURCE_ADDITION_FAILURE_M, MYSQL, "test");

        String datasourceExistsMsgExpr = htmlClassExpression(ISA_ERROR_C, "p");
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(datasourceExistsMsgExpr).hasText(datasourceExistsMessage);

        assertThat($(datasourceExistsMsgExpr).getText(), is(datasourceExistsMessage));
    }
}
