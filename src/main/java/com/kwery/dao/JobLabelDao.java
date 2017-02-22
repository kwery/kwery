package com.kwery.dao;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.services.job.JobSearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobLabelDao {
    private final Provider<EntityManager> entityManagerProvider;
    private final JobDao jobDao;

    @Inject
    public JobLabelDao(Provider<EntityManager> entityManagerProvider, JobDao jobDao) {
        this.entityManagerProvider = entityManagerProvider;
        this.jobDao = jobDao;
    }

    @Transactional
    public JobLabelModel save(JobLabelModel m) {
        EntityManager e = entityManagerProvider.get();

        if (m.getId() != null && m.getId() > 0) {
            m = e.merge(m);
        } else {
            e.persist(m);
        }

        //TODO - There should be a better way to do this
        //Below is done so that Hibernate search reindexes associated JobModels
        updateAssociatedJobModels(m);

        return m;
    }

    private void updateAssociatedJobModels(JobLabelModel m) {
        JobSearchFilter filter = new JobSearchFilter();
        filter.setJobLabelIds(ImmutableSet.of(m.getId()));

        List<JobModel> jobModels = jobDao.filterJobs(filter);

        Integer jobLabelId = m.getId();
        for (JobModel jobModel : jobModels) {

            jobModel.getLabels().removeIf(model -> model.getId().equals(jobLabelId));
            jobModel.getLabels().add(m);

            jobDao.save(jobModel);
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

    @Transactional
    public List<JobLabelModel> getAllJobLabelModels() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<JobLabelModel> cq = cb.createQuery(JobLabelModel.class);
        Root<JobLabelModel> rootEntry = cq.from(JobLabelModel.class);

        CriteriaQuery<JobLabelModel> all = cq.select(rootEntry);
        all.orderBy(cb.asc(rootEntry.get("id")));

        TypedQuery<JobLabelModel> allQuery = e.createQuery(all);
        return allQuery.getResultList();
    }

    @Transactional
    public void deleteJobLabelById(int jobLabelId) {
        EntityManager e = entityManagerProvider.get();
        e.remove(getJobLabelModelById(jobLabelId));
    }

    @Transactional
    public boolean isParentLabel(int jobLabelId) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<JobLabelModel> cq = cb.createQuery(JobLabelModel.class);
        Root<JobLabelModel> root = cq.from(JobLabelModel.class);
        cq.where(cb.equal(root.get("parentLabel").get("id"), jobLabelId));
        TypedQuery<JobLabelModel> tq = m.createQuery(cq);
        List<JobLabelModel> models = tq.getResultList();
        return !models.isEmpty();
    }

    @Transactional
    public boolean doJobsDependOnLabel(int jobLabelId) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder cb = m.getCriteriaBuilder();
        CriteriaQuery<JobModel> cq = cb.createQuery(JobModel.class);
        Root<JobModel> jobModel = cq.from(JobModel.class);

        Join<JobModel, JobLabelModel> join = jobModel.join("labels");
        cq.where(cb.equal(join.get("id"), jobLabelId));

        TypedQuery<JobModel> tq = m.createQuery(cq);
        return !tq.getResultList().isEmpty();
    }
}
