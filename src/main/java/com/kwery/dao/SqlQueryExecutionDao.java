package com.kwery.dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.SqlQueryExecution;
import ninja.jpa.UnitOfWork;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;

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
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionId(executionId);
        List<SqlQueryExecution> executions = filter(filter);

        if (executions.size() == 0) {
            return null;
        }

        return executions.get(0);
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

    @UnitOfWork
    public long count(SqlQueryExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<Long> q = c.createQuery(Long.class);

        Root<SqlQueryExecution> root = q.from(SqlQueryExecution.class);

        q.select(c.count(root));

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

        return m.createQuery(q).getSingleResult();
    }

    @Transactional
    public void deleteBySqlQueryId(int sqlQueryId) {
        EntityManager m = entityManagerProvider.get();
        m.createQuery(
                "delete from SqlQueryExecution e where e.sqlQuery.id = :sqlQueryId")
                .setParameter("sqlQueryId", sqlQueryId
        ).executeUpdate();
    }

    @UnitOfWork
    public List<SqlQueryExecution> lastSuccessfulExecution(List<Integer> sqlQueryIds) {
        //TODO - Simplify
        EntityManager m = entityManagerProvider.get();

        List<SqlQueryExecution> sqlQueryExecutions = new LinkedList<>();

        for (Integer sqlQueryId : sqlQueryIds) {
            SqlQueryExecution sqlQueryExecution = m.createQuery(
                    "select e from SqlQueryExecution e where e.status = :status and e.sqlQuery.id = :sqlQueryId and e.executionEnd is not null order by e.executionEnd desc", SqlQueryExecution.class

            ).setParameter("sqlQueryId", sqlQueryId)
                    .setParameter("status", SUCCESS)
                    .setMaxResults(1)
                    .getSingleResult();

            if (sqlQueryExecution != null) {
                sqlQueryExecutions.add(sqlQueryExecution);
            }
        }

        return sqlQueryExecutions;
    }
}
