package controllers.apis.integration.queryrunapicontroller;

import com.google.common.collect.ImmutableMap;
import com.jayway.jsonpath.Configuration;
import controllers.apis.SqlQueryApiController;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import ninja.Router;
import org.junit.Test;
import views.ActionResult;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryKillQueryExecutionNotFoundTest extends AbstractPostLoginApiTest {
    @Test
    public void test() {
        SqlQueryApiController.SqlQueryExecutionIdContainer sqlQueryExecutionIdContainer = new SqlQueryApiController.SqlQueryExecutionIdContainer();
        sqlQueryExecutionIdContainer.setSqlQueryExecutionId("foo");

        String url = getInjector().getInstance(Router.class).getReverseRoute(
                SqlQueryApiController.class,
                "killSqlQuery",
                ImmutableMap.of(
                        "sqlQueryId", 1
                )
        );

        String jsonResponse = ninjaTestBrowser.postJson(getUrl(url), sqlQueryExecutionIdContainer);

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());

        assertThat(json, hasJsonPath("$.status", is(ActionResult.Status.failure.name())));
        assertThat(json, hasJsonPath("$.messages", is(empty())));
    }
}
