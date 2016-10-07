package controllers.apis.integration.sqlqueryapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.SqlQueryDao;
import dtos.SqlQueryDto;
import org.junit.Before;
import org.junit.Test;
import util.Messages;
import views.ActionResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static conf.Routes.ADD_SQL_QUERY_API;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static util.Messages.CRON_EXPRESSION_VALIDATION_M;
import static util.Messages.LABEL_VALIDATION_M;
import static util.Messages.QUERY_VALIDATION_M;

public class AddSqlQueryValidationTest extends AbstractPostLoginApiTest {
    protected SqlQueryDao dao;

    @Before
    public void setUpAddQueryRunValidationTest() {
        dao = getInjector().getInstance(SqlQueryDao.class);
    }

    protected static final Map<String, List<String>> EXPECTED_MESSAGE_MAP = ImmutableMap.of(
            "query", ImmutableList.of(QUERY_VALIDATION_M),
            "label", ImmutableList.of(LABEL_VALIDATION_M),
            "cronExpression", ImmutableList.of(CRON_EXPRESSION_VALIDATION_M),
            "datasourceId", ImmutableList.of(Messages.DATASOURCE_VALIDATION_M)
    );

    @Test
    public void testNull() throws IOException {
        SqlQueryDto q = new SqlQueryDto();
        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_SQL_QUERY_API), q));
        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);
        assertThat(dao.getByLabel(q.getLabel()), nullValue());
    }

    @Test
    public void testEmpty() throws IOException {
        SqlQueryDto q = new SqlQueryDto();
        q.setQuery("");
        q.setCronExpression("");
        q.setLabel("");
        q.setDatasourceId(0);

        ActionResult actionResult = actionResult(ninjaTestBrowser.postJson(getUrl(ADD_SQL_QUERY_API), q));
        assertFailure(actionResult, EXPECTED_MESSAGE_MAP);
        assertThat(dao.getByLabel(q.getLabel()), nullValue());
    }
}
