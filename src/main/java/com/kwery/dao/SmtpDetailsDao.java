package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.SmtpDetails;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class SmtpDetailsDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SmtpDetails smtpDetails) {
        EntityManager e = entityManagerProvider.get();
        e.persist(smtpDetails);
        e.flush();
    }

    @UnitOfWork
    public List<SmtpDetails> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<SmtpDetails> cq = cb.createQuery(SmtpDetails.class);
        Root<SmtpDetails> root = cq.from(SmtpDetails.class);
        CriteriaQuery<SmtpDetails> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }
}
