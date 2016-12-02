package com.kwery.services.job;

import java.util.concurrent.CountDownLatch;

public interface SqlQueryTaskFactory {
    SqlQueryTask create(int sqlQueryId, CountDownLatch latch);
}
