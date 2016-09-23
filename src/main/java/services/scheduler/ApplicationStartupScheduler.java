package services.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dao.DatasourceDao;
import dao.QueryRunDao;
import it.sauronsoftware.cron4j.Scheduler;
import models.Datasource;
import models.QueryRun;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ApplicationStartupScheduler {
    protected static Logger logger = LoggerFactory.getLogger(ApplicationStartupScheduler.class);

    @Inject
    protected Scheduler scheduler;

    @Inject
    protected DatasourceDao datasourceDao;

    @Inject
    protected QueryRunDao queryRunDao;

    @Inject
    protected QueryTaskFactory queryTaskFactory;

    @Start
    public void schedule() {
        logger.info("Starting schduler");
        scheduler.start();
        logger.info("Scheduling queries on startup");
        for (QueryRun queryRun : queryRunDao.getAll()) {
            Datasource datasource = datasourceDao.getById(queryRun.getDatasource().getId());
            scheduler.schedule(queryRun.getCronExpression(), queryTaskFactory.create(queryRun));
            logger.info("Scheduled query {} connection to datasource {} with cron expression {}", queryRun.getQuery(), datasource.getLabel(), queryRun.getCronExpression());
        }
    }

    @Dispose
    public void stopScheduler() {
        scheduler.stop();
    }
}
