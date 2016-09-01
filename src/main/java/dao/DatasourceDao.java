package dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import models.Datasource;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class DatasourceDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(Datasource d) {
        EntityManager m = entityManagerProvider.get();
        m.persist(d);
        m.flush();
    }

    @UnitOfWork
    public Datasource getByLabel(String label) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<Datasource> cq = cb.createQuery(Datasource.class);
        Root<Datasource> root = cq.from(Datasource.class);
        cq.where(cb.equal(root.get("label"), label));
        TypedQuery<Datasource> tq = m.createQuery(cq);
        List<Datasource> datasources = tq.getResultList();
        if (datasources.isEmpty()) {
            return null;
        } else {
            if (datasources.size() > 1) {
                throw new AssertionError(String.format("More than one datasource with label %s present in datasource table", label));
            }
            return datasources.get(0);
        }
    }

    @UnitOfWork
    public Datasource getById(Integer id) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<Datasource> cq = cb.createQuery(Datasource.class);
        Root<Datasource> root = cq.from(Datasource.class);
        cq.where(cb.equal(root.get("id"), id));
        TypedQuery<Datasource> tq = m.createQuery(cq);
        List<Datasource> datasources = tq.getResultList();
        if (datasources.isEmpty()) {
            return null;
        } else {
            return datasources.get(0);
        }
    }
}
