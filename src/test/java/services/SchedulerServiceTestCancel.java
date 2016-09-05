package services;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import it.sauronsoftware.cron4j.Scheduler;
import models.Datasource;
import models.QueryRun;
import ninja.NinjaDaoTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.QueryTask;
import services.scheduler.QueryTaskFactory;
import services.scheduler.SchedulerService;
import util.TestUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static util.TestUtil.datasource;
import static util.TestUtil.waitForMysql;

public class SchedulerServiceTestCancel extends NinjaDaoTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected Scheduler scheduler;
    protected String taskId;
    protected SchedulerService schedulerService;

    @Before
    public void setUpSchedulerServiceTestCancel() throws IOException {
        datasource = datasource();

        cloudHost = CloudHostFactory.getCloudHost("mysql");
        cloudHost.setup();

        String mysqlHost = cloudHost.getHostName();
        datasource.setUrl(mysqlHost);

        int port = cloudHost.getPort(datasource.getPort());
        datasource.setPort(port);

        if (!waitForMysql(mysqlHost, port)) {
            fail("MySQL docker service is not up");
        }

        scheduler = new Scheduler();
        scheduler.start();

        schedulerService = new SchedulerService();
        schedulerService.setScheduler(scheduler);

        QueryRun queryRun = TestUtil.queryRun();
        queryRun.setQuery("select sleep(86400)");
        queryRun.setCronExpression("*/5 * * * *");
        queryRun.setDatasource(datasource);

        taskId = scheduler.schedule(queryRun.getCronExpression(), new QueryTaskFactory() {
            @Override
            public QueryTask create(QueryRun q, Datasource d) {
                return new QueryTask(datasource, q);
            }
        }.create(queryRun, datasource));
    }

    @Test
    public void test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(90);
        assertThat(scheduler.getExecutingTasks().length, is(1));
        schedulerService.cancel(taskId);
        TimeUnit.SECONDS.sleep(90);
        assertThat(scheduler.getExecutingTasks().length, is(0));
    }

    @After
    public void tearDownSchedulerServiceTestCancel() {
        scheduler.stop();
        cloudHost.teardown();
    }
}
