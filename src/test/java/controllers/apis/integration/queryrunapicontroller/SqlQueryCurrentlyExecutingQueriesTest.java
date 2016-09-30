package controllers.apis.integration.queryrunapicontroller;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import dao.SqlQueryDao;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static java.lang.String.format;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertThat;

public class SqlQueryCurrentlyExecutingQueriesTest extends AbstractPostLoginApiTest {
    protected long start = System.currentTimeMillis();

    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected SqlQuery sqlQuery;

    @Before
    public void setUpQueryRunCurrentlyExecutingQueriesTest() {
        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();
        String mysqlHost = cloudHost.getHostName();
        int port = cloudHost.getPort(3306);

        if (!TestUtil.waitForMysql(mysqlHost, port)) {
            fail("MySQL docker service is not up");
        }

        datasource = new Datasource();
        datasource.setUrl(mysqlHost);
        datasource.setPort(port);
        datasource.setLabel("test");
        datasource.setUsername("root");
        datasource.setPassword("root");

        getInjector().getInstance(DatasourceDao.class).save(datasource);

        sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setCronExpression("* * * * *");
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select sleep(86440)");

        getInjector().getInstance(SqlQueryDao.class).save(sqlQuery);

        getInjector().getInstance(SchedulerService.class).schedule(sqlQuery);
    }

    @Test
    public void test() throws InterruptedException, IOException {
        TimeUnit.SECONDS.sleep(70);
        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(Routes.CURRENTLY_EXECUTING_SQL_QUERY_API));
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, SqlQueryExecution.class);
        List<SqlQueryExecution> sqlQueryExecutions = mapper.readValue(jsonResponse, type);

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());

        List<Long> startTimes = new ArrayList<>(sqlQueryExecutions.size());

        for (int i = 0; i < sqlQueryExecutions.size(); ++i) {
            assertThat(json, hasJsonPath(format("$[%d].id", i), greaterThan(0)));
            assertThat(json, hasJsonPath(format("$[%d].executionStart", i), greaterThan(start)));

            startTimes.add(JsonPath.read(json, format("$[%d].executionStart", i)));

            assertThat(json, hasJsonPath(format("$[%d].executionEnd", i), nullValue()));
            assertThat(json, hasJsonPath(format("$[%d].status", i), is(SqlQueryExecution.Status.ONGOING.name())));
            assertThat(json, hasJsonPath(format("$[%d].result", i), nullValue()));
            assertThat(json, hasJsonPath(format("$[%d].sqlQuery.id", i), greaterThan(0)));
            assertThat(json, hasJsonPath(format("$[%d].sqlQuery.query", i), is(sqlQuery.getQuery())));
            assertThat(json, hasJsonPath(format("$[%d].sqlQuery.label", i), is(sqlQuery.getLabel())));
            assertThat(json, hasJsonPath(format("$[%d].sqlQuery.cronExpression", i), is(sqlQuery.getCronExpression())));
            assertThat(json, hasJsonPath(format("$[%d].sqlQuery.datasource.label", i), is(datasource.getLabel())));
        }

        for (int i = 0; i < startTimes.size() - 1; ++i) {
            assertThat(startTimes.get(i), lessThan(startTimes.get(i + 1)));
        }
    }

    @After
    public void tearDownQueryRunCurrentlyExecutingQueriesTest() {
        cloudHost.teardown();
    }
}
