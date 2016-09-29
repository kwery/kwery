package services.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import dao.QueryRunExecutionDao;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import models.Datasource;
import models.QueryRun;
import models.QueryRunExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.RepoDashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class QueryTask extends Task {
    private static Logger logger = LoggerFactory.getLogger(QueryTask.class);

    private QueryRun queryRun;

    private final QueryRunExecutionDao queryRunExecutionDao;
    private final RepoDashUtil repoDashUtil;
    private final ResultSetProcessorFactory resultSetProcessorFactory;
    private final PreparedStatementExecutorFactory preparedStatementExecutorFactory;

    @Inject
    public QueryTask(QueryRunExecutionDao queryRunExecutionDao, RepoDashUtil repoDashUtil, PreparedStatementExecutorFactory preparedStatementExecutorFactory, ResultSetProcessorFactory resultSetProcessorFactory, @Assisted QueryRun queryRun) {
        this.queryRun = queryRun;
        this.repoDashUtil = repoDashUtil;
        this.preparedStatementExecutorFactory = preparedStatementExecutorFactory;
        this.queryRunExecutionDao = queryRunExecutionDao;
        this.resultSetProcessorFactory = resultSetProcessorFactory;
    }

    @Override
    public void execute(TaskExecutionContext context) {
        Datasource datasource = queryRun.getDatasource();
        logger.info("Starting query {} on datasource {}", queryRun.getQuery(), datasource.getLabel());
        try (Connection connection = repoDashUtil.connection(datasource);
            PreparedStatement p = connection.prepareStatement(queryRun.getQuery())) {

            Future<ResultSet> queryFuture = preparedStatementExecutorFactory.create(p).execute();

            try (ResultSet rs = queryFuture.get()) {
                String result = resultSetProcessorFactory.create(rs).process();
                QueryRunExecution queryRunExecution = queryRunExecutionDao.getByExecutionId(context.getTaskExecutor().getGuid());
                queryRunExecution.setResult(result);
                queryRunExecutionDao.update(queryRunExecution);
            } catch (JsonProcessingException e) {
                logger.error("JSON processing exception while processing result of query {} running on datasource {}", queryRun.getQuery(), datasource.getLabel(), e);
            } catch (InterruptedException e) {
                logger.error("Query {} running on datasource {} cancelled, hence cancelling the prepared statement", queryRun.getQuery(), datasource.getLabel(), e);
                p.cancel();
            } catch (ExecutionException e) {
                logger.error("Exception while trying to retrieve result set of query {} running on datasource {}", queryRun.getQuery(), datasource.getLabel(), e);
            }

        } catch (SQLException e) {
            logger.error("Exception while running query {} on datasource {}", queryRun.getQuery(), datasource.getLabel(), e);
        }
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }

    public QueryRun getQueryRun() {
        return queryRun;
    }
}
