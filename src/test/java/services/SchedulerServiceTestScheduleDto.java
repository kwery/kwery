package services;

import com.google.inject.Provider;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import dao.DatasourceDao;
import dtos.QueryRunDto;
import models.Datasource;
import models.QueryRun;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.scheduler.SchedulerService;
import util.RepoDashTestBase;
import util.TestUtil;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class SchedulerServiceTestScheduleDto extends RepoDashTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected QueryRun queryRun = new QueryRun();
    protected SchedulerService schedulerService;
    protected SchedulerService schedulerServiceSpy;

    @Before
    public void setUpSchedulerServiceTestScheduleDto() {
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

        getInstance(DatasourceDao.class).save(datasource);

        schedulerService = getInstance(SchedulerService.class);
        schedulerServiceSpy = spy(schedulerService);
        schedulerServiceSpy.setQueryRunProvider(new Provider<QueryRun>() {
            @Override
            public QueryRun get() {
                return queryRun;
            }
        });
    }

    @Test
    public void test() {
        QueryRunDto dto = new QueryRunDto();
        dto.setDatasourceId(datasource.getId());
        dto.setQuery("test query");
        dto.setLabel("test label");
        dto.setCronExpression("test cron expression");

        doNothing().when(schedulerServiceSpy).schedule(queryRun);

        schedulerServiceSpy.schedule(dto);

        assertThat(queryRun.getQuery(), is(dto.getQuery()));
        assertThat(queryRun.getLabel(), is(dto.getLabel()));
        assertThat(queryRun.getCronExpression(), is(dto.getCronExpression()));
        assertThat(queryRun.getDatasource().getId(), is(datasource.getId()));
    }


    @After
    public void tearDownSchedulerServiceTestScheduleDto() {
        cloudHost.teardown();
    }
}
