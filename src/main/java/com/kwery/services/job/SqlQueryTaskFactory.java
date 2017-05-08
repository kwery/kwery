package com.kwery.services.job;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public interface SqlQueryTaskFactory {
    SqlQueryTask create(int sqlQueryId, String jobExecutionId, CountDownLatch latch, Map<String, ?> parameters);
}
