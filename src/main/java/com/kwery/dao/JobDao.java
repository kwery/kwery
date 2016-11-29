package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobModel;

import javax.persistence.EntityManager;

public class JobDao {
    protected Provider<EntityManager> entityManagerProvider;

    @Inject
    public JobDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public JobModel save(JobModel m) {
        EntityManager e = entityManagerProvider.get();

        if (m.getId() != null && m.getId() > 0) {
            return e.merge(m);
        } else {
            e.persist(m);
            return m;
        }
    }
}
