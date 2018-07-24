package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.ReportEmailConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class ReportEmailConfigurationDao {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public ReportEmailConfigurationDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public synchronized ReportEmailConfigurationModel save(ReportEmailConfigurationModel m) {
        EntityManager e = entityManagerProvider.get();

        long now = System.currentTimeMillis();
        m.setUpdated(System.currentTimeMillis());

        if (m.getId() != null && m.getId() > 0) {
            m.setCreated(getById(m.getId()).getCreated());
            return e.merge(m);
        } else {
            m.setCreated(now);
            e.persist(m);
            return m;
        }
    }

    @Transactional
    private ReportEmailConfigurationModel getById(Integer id) {
        EntityManager e = entityManagerProvider.get();
        return e.find(ReportEmailConfigurationModel.class, id);
    }

    @Transactional
    public ReportEmailConfigurationModel get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<ReportEmailConfigurationModel> cq = cb.createQuery(ReportEmailConfigurationModel.class);
        Root<ReportEmailConfigurationModel> root = cq.from(ReportEmailConfigurationModel.class);
        CriteriaQuery<ReportEmailConfigurationModel> all = cq.select(root);
        List<ReportEmailConfigurationModel> ms = e.createQuery(all).getResultList();
        if (ms.isEmpty()) {
            return null;
        } else if (ms.size() > 1) {
            logger.error("{} report email configuration models found", ms.size());
            throw new RuntimeException("More than one report email configuration found");
        }
        return ms.get(0);
    }
}
