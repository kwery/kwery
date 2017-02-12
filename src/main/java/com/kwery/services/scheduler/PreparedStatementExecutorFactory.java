package com.kwery.services.scheduler;

import java.sql.PreparedStatement;

public interface PreparedStatementExecutorFactory {
    PreparedStatementExecutor create(PreparedStatement p);
}
