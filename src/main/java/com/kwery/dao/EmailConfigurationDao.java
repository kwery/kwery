package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.EmailConfiguration;

import javax.persistence.EntityManager;

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
}
