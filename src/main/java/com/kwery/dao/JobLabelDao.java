package com.kwery.dao;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobLabelModel;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobLabelDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public JobLabelModel save(JobLabelModel m) {
        EntityManager e = entityManagerProvider.get();

        if (m.getId() != null && m.getId() > 0) {
            return e.merge(m);
        } else {
            e.persist(m);
            return m;
        }
    }

    @Transactional
    public JobLabelModel getJobLabelModelById(int id) {
        EntityManager e = entityManagerProvider.get();
        return e.find(JobLabelModel.class, id);
    }

    @Transactional
    public JobLabelModel getJobLabelModelByLabel(String label) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<JobLabelModel> cq = cb.createQuery(JobLabelModel.class);
        Root<JobLabelModel> root = cq.from(JobLabelModel.class);
        cq.where(cb.equal(root.get("label"), label));
        TypedQuery<JobLabelModel> tq = m.createQuery(cq);
        List<JobLabelModel> models = tq.getResultList();
        if (models.isEmpty()) {
            return null;
        } else {
            if (models.size() > 1) {
                throw new AssertionError(String.format("More than one joblabel with label %s present in %s table", label, JobLabelModel.JOB_LABEL_TABLE));
            }
            return models.get(0);
        }
    }
}
