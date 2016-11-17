package com.kwery.services.scheduler;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;

import java.util.Collection;

//TODO - See the feasibility of removing explicit locks
@Singleton
public class SqlQueryTaskSchedulerHolder {
    private Multimap<Integer, SqlQueryTaskScheduler> map = ArrayListMultimap.create();

    public synchronized boolean add(int sqlQueryId, SqlQueryTaskScheduler sqlQueryTaskScheduler) {
        return map.put(sqlQueryId, sqlQueryTaskScheduler);
    }

    public synchronized boolean remove(int sqlQueryId) {
       return map.removeAll(sqlQueryId).size() > 0;
    }

    public synchronized boolean remove(int sqlQueryId, SqlQueryTaskScheduler sqlQueryTaskScheduler) {
        return map.remove(sqlQueryId, sqlQueryTaskScheduler);
    }

    public synchronized Collection<SqlQueryTaskScheduler> get(int sqlQueryId) {
        return map.get(sqlQueryId);
    }

    public Collection<SqlQueryTaskScheduler> all() {
        return map.values();
    }
}
