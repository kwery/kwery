package controllers.apis.integration.queryrunapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.QueryRunDao;
import dtos.QueryRunDto;
import org.junit.Before;
import org.junit.Test;
import util.Messages;
import views.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static conf.Routes.ADD_QUERY_RUN_API;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.CRON_EXPRESSION_VALIDATION_M;
import static util.Messages.LABEL_VALIDATION_M;
import static util.Messages.QUERY_VALIDATION_M;

public class AddQueryRunValidationTest extends AbstractPostLoginApiTest {
    protected QueryRunDao dao;

    @Before
    public void setUpAddQueryRunValidationTest() {
        dao = getInjector().getInstance(QueryRunDao.class);
    }

    protected static final Map<String, List<String>> EXPECTED_MESSAGE_MAP = ImmutableMap.of(
            "query", ImmutableList.of(QUERY_VALIDATION_M),
            "label", ImmutableList.of(LABEL_VALIDATION_M),
            "cronExpression", ImmutableList.of(CRON_EXPRESSION_VALIDATION_M),
            "datasourceId", ImmutableList.of(Messages.DATASOURCE_VALIDATION_M)
    );

    @Test
    public void testNull() throws IOException {
        QueryRunDto q = new QueryRunDto();
        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_QUERY_RUN_API), q));
        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);
        assertThat(dao.getByLabel(q.getLabel()), nullValue());
    }

    @Test
    public void testEmpty() throws IOException {
        QueryRunDto q = new QueryRunDto();
        q.setQuery("");
        q.setCronExpression("");
        q.setLabel("");
        q.setDatasourceId(0);

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_QUERY_RUN_API), q));
        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);
        assertThat(dao.getByLabel(q.getLabel()), nullValue());
    }
}
