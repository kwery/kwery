package dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.SqlQueryExecution;
import models.SqlQueryExecution.Status;
import ninja.jpa.UnitOfWork;
import services.scheduler.SqlQueryExecutionSearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
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

    @UnitOfWork
    public List<SqlQueryExecution> filter(SqlQueryExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder c = m.getCriteriaBuilder();

        CriteriaQuery<SqlQueryExecution>  q = c.createQuery(SqlQueryExecution.class);
        Root<SqlQueryExecution> root = q.from(SqlQueryExecution.class);

        List<Predicate> predicates = new LinkedList<>();

        if (filter.getSqlQueryId() != 0) {
            predicates.add(c.equal(root.get("sqlQuery").get("id"), filter.getSqlQueryId()));
        }

        if (filter.getExecutionStartStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionStart"), filter.getExecutionStartStart()));
        }

        if (filter.getExecutionStartEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionStart"), filter.getExecutionStartEnd()));
        }

        if (filter.getExecutionEndStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionEnd"), filter.getExecutionEndStart()));
        }

        if (filter.getExecutionEndEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionEnd"), filter.getExecutionEndEnd()));
        }

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            predicates.add(root.get("status").in(filter.getStatuses()));
        }

        if (!"".equals(Strings.nullToEmpty(filter.getExecutionId()))) {
            predicates.add(c.equal(root.get("executionId"), filter.getExecutionId()));
        }

        q.where(predicates.toArray(new Predicate[]{}));

        TypedQuery<SqlQueryExecution> tq = m.createQuery(q)
                .setMaxResults(filter.getResultCount())
                .setFirstResult(filter.getPageNumber() * filter.getResultCount());

        return tq.getResultList();
    }
}
