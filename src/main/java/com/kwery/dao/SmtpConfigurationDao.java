package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.SmtpConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class SmtpConfigurationDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SmtpConfiguration smtpConfiguration) {
        EntityManager e = entityManagerProvider.get();

        if (smtpConfiguration.getId() != null && smtpConfiguration.getId() > 0) {
            e.merge(smtpConfiguration);
        } else {
            e.persist(smtpConfiguration);
        }

        e.flush();
    }

    @Transactional
    public SmtpConfiguration get(int id) {
        return entityManagerProvider.get().find(SmtpConfiguration.class, id);
    }

    @Transactional
    public List<SmtpConfiguration> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<SmtpConfiguration> cq = cb.createQuery(SmtpConfiguration.class);
        Root<SmtpConfiguration> root = cq.from(SmtpConfiguration.class);
        CriteriaQuery<SmtpConfiguration> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }
}
