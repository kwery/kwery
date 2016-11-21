package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.SmtpDetails;

import javax.persistence.EntityManager;

public class SmtpDetailsDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SmtpDetails smtpDetails) {
        EntityManager e = entityManagerProvider.get();
        e.persist(smtpDetails);
        e.flush();
    }
}
