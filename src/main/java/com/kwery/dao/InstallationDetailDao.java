package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.InstallationDetailModel;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Singleton
public class InstallationDetailDao {
    protected final Provider<EntityManager> entityManagerProvider;

    @Inject
    public InstallationDetailDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public InstallationDetailModel get() {
        EntityManager e = entityManagerProvider.get();

        CriteriaQuery<InstallationDetailModel> c = e.getCriteriaBuilder().createQuery(InstallationDetailModel.class);
        c.select(c.from(InstallationDetailModel.class));

        List<InstallationDetailModel> models = e.createQuery(c).getResultList();

        if (models.size() > 1) {
            throw new AssertionError("Installation detail table cannot have more than one row");
        }

        if (models.isEmpty()) {
            return null;
        } else {
            return models.get(0);
        }
    }
}
