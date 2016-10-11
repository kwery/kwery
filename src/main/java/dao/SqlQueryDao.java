package dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.SqlQuery;
import models.SqlQueryExecution;
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

public class SqlQueryDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SqlQuery q) {
        EntityManager m = entityManagerProvider.get();
        m.persist(q);
        m.flush();
    }

    @Transactional
    public void update(SqlQuery q) {
        EntityManager m = entityManagerProvider.get();
        m.merge(q);
        m.flush();
    }

    @UnitOfWork
    public SqlQuery getByLabel(String label) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<SqlQuery> cq = cb.createQuery(SqlQuery.class);

        Root<SqlQuery> root = cq.from(SqlQuery.class);
        cq.where(cb.equal(root.get("label"), label));

        TypedQuery<SqlQuery> tq = m.createQuery(cq);

        List<SqlQuery> runs = tq.getResultList();

        if (runs.isEmpty()) {
            return null;
        } else {
            if (runs.size() > 1) {
                throw new AssertionError(String.format("More than one query run present with label %s", label));
            }
            return runs.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public List<SqlQuery> getAll() {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT q FROM SqlQuery q").getResultList();
    }

    @SuppressWarnings("unchecked")
    @UnitOfWork
    public SqlQuery getById(Integer id) {
        EntityManager e = entityManagerProvider.get();
        List<SqlQuery> m = e.createQuery("SELECT q FROM SqlQuery q where q.id = :id").setParameter("id", id).getResultList();
        if (m.size() == 0) {
            return null;
        }
        return m.get(0);
    }

    @UnitOfWork
    public long countSqlQueriesWithDatasourceId(int datasourceId) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<Long> q = c.createQuery(Long.class);

        Root<SqlQuery> root = q.from(SqlQuery.class);

        q.select(c.count(root));

        List<Predicate> predicates = new LinkedList<>();
        predicates.add(c.equal(root.get("datasource").get("id"), datasourceId));

        q.where(predicates.toArray(new Predicate[]{}));

        return m.createQuery(q).getSingleResult();
    }
}
