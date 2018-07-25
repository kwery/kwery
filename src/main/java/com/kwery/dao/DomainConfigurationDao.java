package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.UrlConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class DomainConfigurationDao {
    protected final Provider<EntityManager> entityManagerProvider;

    @Inject
    public DomainConfigurationDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public synchronized UrlConfiguration save(UrlConfiguration setting) {
        EntityManager e = entityManagerProvider.get();

        if (setting.getId() != null && setting.getId() > 0) {
            e.merge(setting);
        } else {
            e.persist(setting);
        }

        e.flush();

        return setting;
    }

    @Transactional
    public List<UrlConfiguration> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<UrlConfiguration> cq = cb.createQuery(UrlConfiguration.class);
        Root<UrlConfiguration> root = cq.from(UrlConfiguration.class);
        CriteriaQuery<UrlConfiguration> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }
}
