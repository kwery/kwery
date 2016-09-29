package controllers.apis.integration.queryrunapicontroller;

import com.jayway.jsonpath.Configuration;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import conf.Routes;
import controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import dao.DatasourceDao;
import dao.QueryRunDao;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import util.TestUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static java.lang.String.format;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

public class QueryRunCurrentlyExecutingQueriesTest extends AbstractPostLoginApiTest {
    protected long start = System.currentTimeMillis();

    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected QueryRun queryRun;

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

        queryRun = new QueryRun();
        queryRun.setDatasource(datasource);
        queryRun.setCronExpression("* * * * *");
        queryRun.setLabel("test");
        queryRun.setQuery("select sleep(86440)");

        getInjector().getInstance(QueryRunDao.class).save(queryRun);

        getInjector().getInstance(SchedulerService.class).schedule(queryRun);
    }

    @Test
    public void test() throws InterruptedException, IOException {
        TimeUnit.SECONDS.sleep(70);
        String jsonResponse = ninjaTestBrowser.makeJsonRequest(getUrl(Routes.CURRENTLY_EXECUTING_QUERY_RUN_API));
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(List.class, QueryRunExecution.class);
        List<QueryRunExecution> queryRunExecutions = mapper.readValue(jsonResponse, type);

        Object json = Configuration.defaultConfiguration().jsonProvider().parse(jsonResponse);
        assertThat(json, isJson());

        for (int i = 0; i < queryRunExecutions.size(); ++i) {
            assertThat(json, hasJsonPath(format("$[%d].id", i), greaterThan(0)));
            assertThat(json, hasJsonPath(format("$[%d].executionStart", i), greaterThan(start)));
            assertThat(json, hasJsonPath(format("$[%d].executionEnd", i), nullValue()));
            assertThat(json, hasJsonPath(format("$[%d].status", i), is(QueryRunExecution.Status.ONGOING.name())));
            assertThat(json, hasJsonPath(format("$[%d].result", i), nullValue()));
            assertThat(json, hasJsonPath(format("$[%d].queryRun.id", i), greaterThan(0)));
            assertThat(json, hasJsonPath(format("$[%d].queryRun.query", i), is(queryRun.getQuery())));
            assertThat(json, hasJsonPath(format("$[%d].queryRun.label", i), is(queryRun.getLabel())));
            assertThat(json, hasJsonPath(format("$[%d].queryRun.cronExpression", i), is(queryRun.getCronExpression())));
            assertThat(json, hasJsonPath(format("$[%d].queryRun.datasource.label", i), is(datasource.getLabel())));
        }
    }

    @After
    public void tearDownQueryRunCurrentlyExecutingQueriesTest() {
        cloudHost.teardown();
    }
}
