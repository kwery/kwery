package services.scheduler;

import models.Datasource;
import models.SqlQuery;

import java.sql.SQLException;

public interface SqlQueryRunner {
    void run(Datasource datasource, SqlQuery sqlQuery) throws SQLException;
}
