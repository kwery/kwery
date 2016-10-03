package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.SqlQueryExecution;
import models.SqlQueryExecution.Status;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import java.util.List;

public class SqlQueryExecutionDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SqlQueryExecution e) {
        EntityManager m = entityManagerProvider.get();
        m.persist(e);
        m.flush();
    }

    @Transactional
    public void update(SqlQueryExecution e) {
        EntityManager m = entityManagerProvider.get();
        m.merge(e);
        m.flush();
    }

    @UnitOfWork
    public SqlQueryExecution getById(Integer id) {
        return entityManagerProvider.get().find(SqlQueryExecution.class, id);
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public SqlQueryExecution getByExecutionId(String executionId) {
        EntityManager m = entityManagerProvider.get();
        List<SqlQueryExecution> l = m.createQuery("SELECT e FROM SqlQueryExecution e WHERE e.executionId = :executionId")
                .setParameter("executionId", executionId).getResultList();

        if (l.size() == 0) {
            return null;
        }

        return l.get(0);
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<SqlQueryExecution> getBySqlQueryId(Integer id) {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT e FROM SqlQueryExecution e WHERE e.sqlQuery.id = :sqlQueryId").setParameter("sqlQueryId", id).getResultList();
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<SqlQueryExecution> getByStatus(Status status) {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT e FROM SqlQueryExecution e WHERE e.status = :status").setParameter("status", status).getResultList();
    }
}
