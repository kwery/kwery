package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.SqlQueryModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class SqlQueryDao {
    protected final Provider<EntityManager> entityManagerProvider;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;

    @Inject
    public SqlQueryDao(Provider<EntityManager> entityManagerProvider, SqlQueryExecutionDao sqlQueryExecutionDao) {
        this.entityManagerProvider = entityManagerProvider;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
    }

    @Transactional
    public SqlQueryModel getByLabel(String label) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<SqlQueryModel> cq = cb.createQuery(SqlQueryModel.class);

        Root<SqlQueryModel> root = cq.from(SqlQueryModel.class);
        cq.where(cb.equal(root.get("label"), label));

        TypedQuery<SqlQueryModel> tq = m.createQuery(cq);

        List<SqlQueryModel> runs = tq.getResultList();

        if (runs.isEmpty()) {
            return null;
        } else {
            if (runs.size() > 1) {
                throw new AssertionError(String.format("More than one query run present with label %s", label));
            }
            return runs.get(0);
        }
    }

    @Transactional
    public List<SqlQueryModel> getAll() {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT q FROM SqlQueryModel q", SqlQueryModel.class).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public SqlQueryModel getById(Integer id) {
        EntityManager e = entityManagerProvider.get();
        List<SqlQueryModel> m = e.createQuery("SELECT q FROM SqlQueryModel q where q.id = :id").setParameter("id", id).getResultList();
        if (m.size() == 0) {
            return null;
        }
        return m.get(0);
    }

    @Transactional
    public long countSqlQueriesWithDatasourceId(int datasourceId) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<Long> q = c.createQuery(Long.class);

        Root<SqlQueryModel> root = q.from(SqlQueryModel.class);

        q.select(c.count(root));

        List<Predicate> predicates = new LinkedList<>();
        predicates.add(c.equal(root.get("datasource").get("id"), datasourceId));

        q.where(predicates.toArray(new Predicate[]{}));

        return m.createQuery(q).getSingleResult();
    }

    @Transactional
    public List<SqlQueryModel> getSqlQueriesWithDatasourceId(int datasourceId) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<SqlQueryModel> q = c.createQuery(SqlQueryModel.class);

        Root<SqlQueryModel> root = q.from(SqlQueryModel.class);

        List<Predicate> predicates = new LinkedList<>();
        predicates.add(c.equal(root.get("datasource").get("id"), datasourceId));

        q.where(predicates.toArray(new Predicate[]{}));

        return m.createQuery(q).getResultList();
    }
}
