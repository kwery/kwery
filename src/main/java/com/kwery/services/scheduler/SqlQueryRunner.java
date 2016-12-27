package com.kwery.services.scheduler;

import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;

import java.sql.SQLException;

public interface SqlQueryRunner {
    void run(Datasource datasource, SqlQueryModel sqlQuery) throws SQLException;
}
