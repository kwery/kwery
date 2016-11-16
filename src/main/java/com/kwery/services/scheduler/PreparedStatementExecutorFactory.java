package com.kwery.services.scheduler;

import java.sql.PreparedStatement;

public interface PreparedStatementExecutorFactory {
    public PreparedStatementExecutor create(PreparedStatement p);
}
