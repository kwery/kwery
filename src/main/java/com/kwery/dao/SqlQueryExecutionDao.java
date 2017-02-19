package com.kwery.dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;

public class SqlQueryExecutionDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SqlQueryExecutionModel e) {
        EntityManager m = entityManagerProvider.get();

        if (e.getId() != null && e.getId() > 0) {
           m.merge(e);
        } else {
            m.persist(e);
        }

        m.flush();
    }

    @UnitOfWork
    public SqlQueryExecutionModel getById(Integer id) {
        return entityManagerProvider.get().find(SqlQueryExecutionModel.class, id);
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public SqlQueryExecutionModel getByExecutionId(String executionId) {
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setExecutionId(executionId);
        List<SqlQueryExecutionModel> executions = filter(filter);

        if (executions.size() == 0) {
            return null;
        }

        if (executions.size() > 1) {
            throw new AssertionError("More than one SqlQueryExecutionModel found with execution id - " + executionId);
        }

        return executions.get(0);
    }

    @UnitOfWork
    public List<SqlQueryExecutionModel> filter(SqlQueryExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder c = m.getCriteriaBuilder();

        CriteriaQuery<SqlQueryExecutionModel>  q = c.createQuery(SqlQueryExecutionModel.class);
        Root<SqlQueryExecutionModel> root = q.from(SqlQueryExecutionModel.class);

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

        if (filter.getResultCount() == 0) {
            return m.createQuery(q).getResultList();
        } else {
            return m.createQuery(q)
                    .setMaxResults(filter.getResultCount())
                    .setFirstResult(filter.getPageNumber() * filter.getResultCount())
                    .getResultList();
        }
    }

    @UnitOfWork
    public long count(SqlQueryExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<Long> q = c.createQuery(Long.class);

        Root<SqlQueryExecutionModel> root = q.from(SqlQueryExecutionModel.class);

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

    @UnitOfWork
    public List<SqlQueryExecutionModel> lastSuccessfulExecution(List<Integer> sqlQueryIds) {
        //TODO - Simplify
        EntityManager m = entityManagerProvider.get();

        List<SqlQueryExecutionModel> sqlQueryExecutions = new LinkedList<>();

        for (Integer sqlQueryId : sqlQueryIds) {
            SqlQueryExecutionModel sqlQueryExecution = m.createQuery(
                    "select e from SqlQueryExecutionModel e where e.status = :status and e.sqlQuery.id = :sqlQueryId and e.executionEnd is not null order by e.executionEnd desc", SqlQueryExecutionModel.class

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
