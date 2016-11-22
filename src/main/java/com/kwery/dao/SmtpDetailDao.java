package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.SmtpDetail;
import ninja.jpa.UnitOfWork;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class SmtpDetailDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public void save(SmtpDetail smtpDetail) {
        EntityManager e = entityManagerProvider.get();

        if (smtpDetail.getId() != null && smtpDetail.getId() > 0) {
            e.merge(smtpDetail);
        } else {
            e.persist(smtpDetail);
        }

        e.flush();
    }

    @UnitOfWork
    public SmtpDetail get(int id) {
        return entityManagerProvider.get().find(SmtpDetail.class, id);
    }

    @UnitOfWork
    public List<SmtpDetail> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<SmtpDetail> cq = cb.createQuery(SmtpDetail.class);
        Root<SmtpDetail> root = cq.from(SmtpDetail.class);
        CriteriaQuery<SmtpDetail> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }
}
