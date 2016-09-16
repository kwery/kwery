package services.scheduler;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import it.sauronsoftware.cron4j.TaskExecutor;
import models.Datasource;
import models.QueryRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class QueryTask extends Task {
    private static Logger logger = LoggerFactory.getLogger(QueryTask.class);

    private Datasource datasource;
    private QueryRun queryRun;
    private long cancelCheckFrequency;

    private String id = UUID.randomUUID().toString();

    @Inject
    public QueryTask(@Assisted Datasource datasource, @Assisted QueryRun queryRun) {
        this(datasource, queryRun, TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES));
    }

    public QueryTask(@Assisted Datasource datasource, @Assisted QueryRun queryRun, @Assisted long cancelCheckFrequency) {
        this.datasource = datasource;
        this.queryRun = queryRun;
        this.cancelCheckFrequency = cancelCheckFrequency;
    }

    @Override
    public void execute(TaskExecutionContext context) {
        logger.info("Starting query {} on datasource {}", queryRun.getQuery(), datasource.getLabel());
        Timer timer = new Timer();
        try (Connection connection = DriverManager.getConnection(
                String.format("jdbc:mysql://%s:%d", datasource.getUrl(), datasource.getPort()), datasource.getUsername(), datasource.getPassword())) {
            PreparedStatement p = connection.prepareStatement(queryRun.getQuery());

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (context.isStopped()) {
                        logger.info("Task execution stopped, cancelling query {} on datasource {}", queryRun.getQuery(), datasource.getLabel());
                        try {
                            if (p != null) {
                                p.cancel();
                                p.close();
                            } else {
                                logger.info("Count not cancel query execution {} on datasource {} because the PreparedStatement is null", queryRun.getQuery(), datasource.getLabel());
                            }
                        } catch (SQLException e) {
                            logger.error("Exception while cancelling query {} running on datasource {}", queryRun.getQuery(), queryRun.getDatasource().getLabel(), e);
                        }
                    }
                }
            }, cancelCheckFrequency, cancelCheckFrequency);
            p.executeQuery();
            p.close();
            timer.cancel();
        } catch (SQLException e) {
            logger.error("Exception while running query {} on datasource {}", queryRun.getQuery(), datasource.getLabel(), e);
        } finally {
            timer.cancel();
        }
    }

    @Override
    public boolean canBeStopped() {
        return true;
    }


    public String id() {
        return id;
    }

    public static void main(String[] args) {
        logger.trace("----------------------");

        Datasource datasource = new Datasource();
        datasource.setUrl("localhost");
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setPort(3306);
        datasource.setType(Datasource.Type.MYSQL);
        datasource.setLabel("test");

        QueryRun queryRun = new QueryRun();
        queryRun.setCronExpression("* * * * *");
        queryRun.setDatasource(datasource);
        queryRun.setLabel("test");
        queryRun.setQuery("select sleep(86400)");

        Scheduler scheduler = new Scheduler();
        scheduler.start();
        String scheduleId = scheduler.schedule(queryRun.getCronExpression(), new QueryTask(datasource, queryRun));
        //scheduler.launch(new QueryTask(datasource, queryRun));


        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info("Cancelling query");

        for (TaskExecutor executor : scheduler.getExecutingTasks()) {
            if (((QueryTask)executor.getTask()).id().equals(((QueryTask)scheduler.getTask(scheduleId)).id())) {
                logger.info("Cancelling query - " + scheduleId);
                executor.stop();
                break;
            }
        }


        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Currently executing tasks - " + scheduler.getExecutingTasks().length);
    }
}
