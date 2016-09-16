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
    protected String scheduleId;
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
        queryRun.setCronExpression("*/2 * * * *");
        queryRun.setDatasource(datasource);

        scheduleId = scheduler.schedule(queryRun.getCronExpression(), new QueryTaskFactory() {
            @Override
            public QueryTask create(QueryRun q, Datasource d, long cancelCheckFrequency) {
                return new QueryTask(datasource, q, cancelCheckFrequency);
            }
        }.create(queryRun, datasource, TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS)));
    }

    @Test
    public void test() throws InterruptedException {
        while (true) {
            long start = System.currentTimeMillis();

            if (scheduler.getExecutingTasks().length > 0) {
                schedulerService.cancel(scheduleId);
                TimeUnit.SECONDS.sleep(10);
                assertThat(scheduler.getExecutingTasks().length, is(0));
                break;
            }

            if (TimeUnit.MINUTES.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS) > 2) {
                break;
            }

            TimeUnit.MILLISECONDS.sleep(1);
        }
    }

    @After
    public void tearDownSchedulerServiceTestCancel() {
        scheduler.stop();
        cloudHost.teardown();
    }
}
