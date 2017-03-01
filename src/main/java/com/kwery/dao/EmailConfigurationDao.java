package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.EmailConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class EmailConfigurationDao {
    protected final Provider<EntityManager> entityManagerProvider;

    @Inject
    public EmailConfigurationDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public void save(EmailConfiguration emailConfiguration) {
        EntityManager e = entityManagerProvider.get();

        if (emailConfiguration.getId() != null && emailConfiguration.getId() > 0) {
            e.merge(emailConfiguration);
        } else {
            e.persist(emailConfiguration);
        }

        e.flush();
    }

    @Transactional
    public List<EmailConfiguration> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<EmailConfiguration> cq = cb.createQuery(EmailConfiguration.class);
        Root<EmailConfiguration> root = cq.from(EmailConfiguration.class);
        CriteriaQuery<EmailConfiguration> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }

    @Transactional
    public EmailConfiguration get(int id) {
        return entityManagerProvider.get().find(EmailConfiguration.class, id);
    }
}
