package com.kwery.dao;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobSearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Singleton
public class DatasourceDao {
    private final Provider<EntityManager> entityManagerProvider;
    private final JobDao jobDao;
    private final SqlQueryDao sqlQueryDao;

    @Inject
    public DatasourceDao(Provider<EntityManager> entityManagerProvider, JobDao jobDao, SqlQueryDao sqlQueryDao) {
        this.entityManagerProvider = entityManagerProvider;
        this.jobDao = jobDao;
        this.sqlQueryDao = sqlQueryDao;
    }

    @Transactional
    public synchronized void save(Datasource d) {
        EntityManager m = entityManagerProvider.get();
        m.persist(d);
        m.flush();
    }

    @Transactional
    public synchronized void update(Datasource d) {
        EntityManager m = entityManagerProvider.get();
        m.merge(d);

        //TODO - There should be a better way to do this
        //Below is done so that Hibernate search reindexes associated JobModels
        for (SqlQueryModel sqlQueryModel : sqlQueryDao.getSqlQueriesWithDatasourceId(d.getId())) {
            sqlQueryModel.setDatasource(d);

            JobSearchFilter filter = new JobSearchFilter();
            filter.setSqlQueryIds(ImmutableSet.of(sqlQueryModel.getId()));
            for (JobModel jobModel : jobDao.filterJobs(filter, FlushModeType.COMMIT)) {
                jobModel.getSqlQueries().removeIf(sqlQueryModel1 -> sqlQueryModel1.getId().equals(sqlQueryModel.getId()));
                jobModel.getSqlQueries().add(sqlQueryModel);
                jobDao.save(jobModel);
            }
        }

        m.flush();
    }

    @Transactional
    public Datasource getByLabel(String label) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<Datasource> cq = cb.createQuery(Datasource.class);
        Root<Datasource> root = cq.from(Datasource.class);
        cq.where(cb.equal(root.get("label"), label));
        TypedQuery<Datasource> tq = m.createQuery(cq);
        List<Datasource> datasources = tq.getResultList();
        if (datasources.isEmpty()) {
            return null;
        } else {
            if (datasources.size() > 1) {
                throw new AssertionError(String.format("More than one datasource with label %s present in datasource table", label));
            }
            return datasources.get(0);
        }
    }

    @Transactional
    public Datasource getById(Integer id) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<Datasource> cq = cb.createQuery(Datasource.class);
        Root<Datasource> root = cq.from(Datasource.class);
        cq.where(cb.equal(root.get("id"), id));
        TypedQuery<Datasource> tq = m.createQuery(cq);
        List<Datasource> datasources = tq.getResultList();
        if (datasources.isEmpty()) {
            return null;
        } else {
            return datasources.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<Datasource> getAll() {
        EntityManager m = entityManagerProvider.get();
        return m.createQuery("SELECT d FROM Datasource d order by d.id asc").getResultList();
    }

    @Transactional
    public void delete(int datasourceId) {
        EntityManager m = entityManagerProvider.get();
        m.remove(m.find(Datasource.class, datasourceId));
    }
}
