package services;

import dao.DatasourceDao;
import dao.QueryRunDao;
import dtos.QueryRunDto;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import models.Datasource;
import models.QueryRun;
import ninja.NinjaDaoTestBase;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.QueryTask;
import services.scheduler.QueryTaskFactory;
import services.scheduler.SchedulerService;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRunDto;

public class SchedulerServiceTestSchedule extends NinjaDaoTestBase {
    protected Datasource datasource;
    protected QueryRunDto queryRunDto;
    protected SchedulerService schedulerService;
    protected QueryTask queryTask;
    protected QueryRunDao queryRunDao;

    @Before
    public void setUpSchedulerServiceTest() {
        datasource = datasource();
        DatasourceDao datasourceDao = getInstance(DatasourceDao.class);
        datasourceDao.save(datasource);

        queryRunDto = queryRunDto();
        queryRunDto.setDatasourceId(datasource.getId());

        schedulerService = new SchedulerService();
        schedulerService.setDatasourceDao(datasourceDao);

        Scheduler scheduler = new Scheduler();
        scheduler.start();
        schedulerService.setScheduler(scheduler);

        queryTask = mock(QueryTask.class);

        queryRunDao = getInstance(QueryRunDao.class);
        schedulerService.setQueryRunDao(queryRunDao);
        schedulerService.setFactory(new TestQueryTaskFactory());
    }

    @Test
    public void test() throws InterruptedException {
        schedulerService.schedule(queryRunDto);
        assertThat(queryRunDao.getAll(), hasSize(1));
        TimeUnit.SECONDS.sleep(90);
        verify(queryTask, atLeastOnce()).execute(any(TaskExecutionContext.class));
    }

    protected class TestQueryTaskFactory implements QueryTaskFactory {
        @Override
        public QueryTask create(QueryRun q, Datasource d, long cancelCheckFrequency) {
            return queryTask;
        }
    }
}
