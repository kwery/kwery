package services.scheduler;

import models.Datasource;
import models.QueryRun;

import java.sql.SQLException;

public interface QueryRunner {
    void run(Datasource datasource, QueryRun queryRun) throws SQLException;
}
