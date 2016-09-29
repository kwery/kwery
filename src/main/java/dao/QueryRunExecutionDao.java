package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.QueryRunExecution;
import models.QueryRunExecution.Status;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import java.util.List;

public class QueryRunExecutionDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(QueryRunExecution e) {
        EntityManager m = entityManagerProvider.get();
        m.persist(e);
        m.flush();
    }

    @Transactional
    public void update(QueryRunExecution e) {
        EntityManager m = entityManagerProvider.get();
        m.merge(e);
        m.flush();
    }

    @UnitOfWork
    public QueryRunExecution getById(Integer id) {
        return entityManagerProvider.get().find(QueryRunExecution.class, id);
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public QueryRunExecution getByExecutionId(String executionId) {
        EntityManager m = entityManagerProvider.get();
        List<QueryRunExecution> l = m.createQuery("SELECT e FROM QueryRunExecution e WHERE e.executionId = :executionId")
                .setParameter("executionId", executionId).getResultList();

        if (l.size() == 0) {
            return null;
        }

        return l.get(0);
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<QueryRunExecution> getByQueryRunId(Integer queryRunId) {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT e FROM QueryRunExecution e WHERE e.queryRun.id = :queryRunId").setParameter("queryRunId", queryRunId).getResultList();
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<QueryRunExecution> getByStatus(Status status) {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT e FROM QueryRunExecution e WHERE e.status = :status").setParameter("status", status).getResultList();
    }
}
