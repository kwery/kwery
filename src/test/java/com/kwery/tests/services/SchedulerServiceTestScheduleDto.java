package com.kwery.tests.services;

import com.google.inject.Provider;
import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.kwery.dao.DatasourceDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.kwery.services.scheduler.SchedulerService;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class SchedulerServiceTestScheduleDto extends RepoDashTestBase {
    protected CloudHost cloudHost;
    protected Datasource datasource;
    protected SqlQuery sqlQuery = new SqlQuery();
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
        schedulerServiceSpy.setQueryRunProvider(new Provider<SqlQuery>() {
            @Override
            public SqlQuery get() {
                return sqlQuery;
            }
        });
    }

    @Test
    public void test() {
        SqlQueryDto dto = new SqlQueryDto();
        dto.setDatasourceId(datasource.getId());
        dto.setQuery("test query");
        dto.setLabel("test label");
        dto.setCronExpression("test cron expression");

        doNothing().when(schedulerServiceSpy).schedule(sqlQuery);

        schedulerServiceSpy.schedule(dto);

        assertThat(sqlQuery.getQuery(), is(dto.getQuery()));
        assertThat(sqlQuery.getLabel(), is(dto.getLabel()));
        assertThat(sqlQuery.getCronExpression(), is(dto.getCronExpression()));
        assertThat(sqlQuery.getDatasource().getId(), is(datasource.getId()));
    }


    @After
    public void tearDownSchedulerServiceTestScheduleDto() {
        cloudHost.teardown();
    }
}
