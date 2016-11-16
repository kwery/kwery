package com.kwery.services.scheduler;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;

import java.sql.SQLException;

public interface SqlQueryRunner {
    void run(Datasource datasource, SqlQuery sqlQuery) throws SQLException;
}
