package com.kwery.dao;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.KweryVersionModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class KweryVersionDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public KweryVersionModel save(KweryVersionModel m) {
        //At any point of time there should be only one version of Kwery in the table
        EntityManager e = entityManagerProvider.get();

        if (m.getId() != null && m.getId() > 0) {
            return e.merge(m);
        } else {
            Preconditions.checkArgument(get() == null, "Cannot save a new KweryVersion when there is one already in the table");
            e.persist(m);
            return m;
        }
    }

    @Transactional
    public KweryVersionModel get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<KweryVersionModel> cq = cb.createQuery(KweryVersionModel.class);
        Root<KweryVersionModel> rootEntry = cq.from(KweryVersionModel.class);
        CriteriaQuery<KweryVersionModel> all = cq.select(rootEntry);
        TypedQuery<KweryVersionModel> allQuery = e.createQuery(all);
        List<KweryVersionModel> models = allQuery.getResultList();
        Preconditions.checkState(models.size() == 1 || models.isEmpty(), "More than one KweryVersionModel found - " + models);
        return models.isEmpty() ? null : models.get(0);
    }
}
