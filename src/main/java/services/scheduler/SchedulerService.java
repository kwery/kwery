package services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import dao.QueryRunDao;
import dtos.QueryRunDto;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.QueryRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SchedulerService {
    private static Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    @Inject
    protected QueryRunDao queryRunDao;

    @Inject
    private DatasourceDao datasourceDao;

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected QueryTaskFactory factory;

    public String schedule(QueryRunDto dto) {
        Datasource datasource = datasourceDao.getById(dto.getDatasourceId());
        QueryRun model = toModel(dto, datasource);
        queryRunDao.save(model);
        String id = scheduler.schedule(model.getCronExpression(), factory.create(model, datasource));
        logger.info("Scheduled query {} with cron expression {} on datasource {}, schedule id is {}", model.getQuery(), model.getCronExpression(), datasource.getLabel(), id);
        return id;
    }

    public boolean cancel(String scheduleId) {
        for (TaskExecutor executor : scheduler.getExecutingTasks()) {
            if (((QueryTask)executor.getTask()).id().equals(((QueryTask)scheduler.getTask(scheduleId)).id())) {
                executor.stop();
                logger.info("Cancelling task with schedule id {}", scheduleId);
                return true;
            }
        }

        logger.info("Could not cancel task with schedule id {} as it is not found", scheduleId);
        return false;
    }

    public QueryRun toModel(QueryRunDto dto, Datasource datasource) {
        QueryRun q = new QueryRun();
        q.setQuery(dto.getQuery());
        q.setCronExpression(dto.getCronExpression());
        q.setLabel(dto.getLabel());
        q.setDatasource(datasource);
        return q;
    }

    public void setQueryRunDao(QueryRunDao queryRunDao) {
        this.queryRunDao = queryRunDao;
    }

    public void setDatasourceDao(DatasourceDao datasourceDao) {
        this.datasourceDao = datasourceDao;
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void setFactory(QueryTaskFactory factory) {
        this.factory = factory;
    }
}
