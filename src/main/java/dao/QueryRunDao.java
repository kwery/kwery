package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.QueryRun;

import javax.persistence.EntityManager;

public class QueryRunDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(QueryRun q) {
        EntityManager m = entityManagerProvider.get();
        m.persist(q);
        m.flush();
    }
}
