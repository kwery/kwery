package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.UrlSetting;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class DomainSettingDao {
    protected final Provider<EntityManager> entityManagerProvider;

    @Inject
    public DomainSettingDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public UrlSetting save(UrlSetting setting) {
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
    public List<UrlSetting> get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<UrlSetting> cq = cb.createQuery(UrlSetting.class);
        Root<UrlSetting> root = cq.from(UrlSetting.class);
        CriteriaQuery<UrlSetting> all = cq.select(root);
        return e.createQuery(all).getResultList();
    }
}
