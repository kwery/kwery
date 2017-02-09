package com.kwery.dao;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobExecutionModel;
import com.kwery.services.job.JobExecutionSearchFilter;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.LinkedList;
import java.util.List;

import static com.kwery.models.JobExecutionModel.Status.SUCCESS;

public class JobExecutionDao {
    @Inject
    private Provider<EntityManager> entityManagerProvider;

    @Transactional
    public JobExecutionModel save(JobExecutionModel e) {
        EntityManager m = entityManagerProvider.get();

        if (e.getId() != null && e.getId() > 0) {
            e = m.merge(e);
        } else {
            m.persist(e);
        }

        m.flush();

        return e;
    }

    @Transactional
    public JobExecutionModel getById(Integer id) {
        return entityManagerProvider.get().find(JobExecutionModel.class, id);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public JobExecutionModel getByExecutionId(String executionId) {
        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setExecutionId(executionId);
        List<JobExecutionModel> executions = filter(filter);

        if (executions.size() == 0) {
            return null;
        }

        return executions.get(0);
    }

    @Transactional
    public List<JobExecutionModel> filter(JobExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder c = m.getCriteriaBuilder();

        CriteriaQuery<JobExecutionModel>  q = c.createQuery(JobExecutionModel.class);
        Root<JobExecutionModel> root = q.from(JobExecutionModel.class);

        List<Predicate> predicates = new LinkedList<>();

        if (filter.getJobId() != 0) {
            predicates.add(c.equal(root.get("jobModel").get("id"), filter.getJobId()));
        }

        if (filter.getExecutionStartStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionStart"), filter.getExecutionStartStart()));
        }

        if (filter.getExecutionStartEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionStart"), filter.getExecutionStartEnd()));
        }

        if (filter.getExecutionEndStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionEnd"), filter.getExecutionEndStart()));
        }

        if (filter.getExecutionEndEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionEnd"), filter.getExecutionEndEnd()));
        }

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            predicates.add(root.get("status").in(filter.getStatuses()));
        }

        if (!"".equals(Strings.nullToEmpty(filter.getExecutionId()))) {
            predicates.add(c.equal(root.get("executionId"), filter.getExecutionId()));
        }

        q.where(predicates.toArray(new Predicate[]{}));
        q.orderBy(c.desc(root.get("executionStart")));

        TypedQuery<JobExecutionModel> tq = m.createQuery(q)
                .setMaxResults(filter.getResultCount())
                .setFirstResult(filter.getPageNumber() * filter.getResultCount());

        return tq.getResultList();
    }

    @Transactional
    public long count(JobExecutionSearchFilter filter) {
        EntityManager m = entityManagerProvider.get();

        CriteriaBuilder c = m.getCriteriaBuilder();
        CriteriaQuery<Long> q = c.createQuery(Long.class);

        Root<JobExecutionModel> root = q.from(JobExecutionModel.class);

        q.select(c.count(root));

        List<Predicate> predicates = new LinkedList<>();

        if (filter.getJobId() != 0) {
            predicates.add(c.equal(root.get("jobModel").get("id"), filter.getJobId()));
        }

        if (filter.getExecutionStartStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionStart"), filter.getExecutionStartStart()));
        }

        if (filter.getExecutionStartEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionStart"), filter.getExecutionStartEnd()));
        }

        if (filter.getExecutionEndStart() != 0) {
            predicates.add(c.greaterThan(root.get("executionEnd"), filter.getExecutionEndStart()));
        }

        if (filter.getExecutionEndEnd() != 0) {
            predicates.add(c.lessThan(root.get("executionEnd"), filter.getExecutionEndEnd()));
        }

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            predicates.add(root.get("status").in(filter.getStatuses()));
        }

        if (!"".equals(Strings.nullToEmpty(filter.getExecutionId()))) {
            predicates.add(c.equal(root.get("executionId"), filter.getExecutionId()));
        }

        q.where(predicates.toArray(new Predicate[]{}));

        return m.createQuery(q).getSingleResult();
    }

    @Transactional
    public void deleteByJobId(int jobId) {
        EntityManager m = entityManagerProvider.get();

        JobExecutionSearchFilter filter = new JobExecutionSearchFilter();
        filter.setJobId(jobId);
        List<JobExecutionModel> jobExecutionModels = filter(filter);

        //TODO - figure out why this is needed
        for (JobExecutionModel jobExecutionModel : jobExecutionModels) {
            jobExecutionModel.getSqlQueryExecutionModels().clear();
        }

        m.flush();

        CriteriaBuilder criteriaBuilder = m.getCriteriaBuilder();
        CriteriaDelete delete = criteriaBuilder.createCriteriaDelete(JobExecutionModel.class);
        Root jobExecutionModel = delete.from(JobExecutionModel.class);
        delete.where(criteriaBuilder.equal(jobExecutionModel.get("jobModel").get("id"), jobId));
        Query query = m.createQuery(delete);
        query.executeUpdate();
    }

    @Transactional
    public List<JobExecutionModel> lastSuccessfulExecution(List<Integer> jobIds) {
        //TODO - Simplify
        EntityManager m = entityManagerProvider.get();

        List<JobExecutionModel> jobExecutions = new LinkedList<>();

        for (Integer jobId : jobIds) {
            JobExecutionModel jobExecutionModel = m.createQuery(
                    "select e from JobExecutionModel e where e.status = :status and e.jobModel.id = :jobModelId and e.executionEnd is not null order by e.executionEnd desc", JobExecutionModel.class

            ).setParameter("jobModelId", jobId)
                    .setParameter("status", SUCCESS)
                    .setMaxResults(1)
                    .getSingleResult();

            if (jobExecutionModel != null) {
                jobExecutions.add(jobExecutionModel);
            }
        }

        return jobExecutions;
    }

    @Transactional
    public void deleteJobExecutions(List<Integer> jobExecutionIds) {
        EntityManager m = entityManagerProvider.get();
        CriteriaBuilder criteriaBuilder  = m.getCriteriaBuilder();
        CriteriaDelete<JobExecutionModel> query = criteriaBuilder.createCriteriaDelete(JobExecutionModel.class);
        Root<JobExecutionModel> root = query.from(JobExecutionModel.class);
        query.where(root.get("id").in(jobExecutionIds));
        m.createQuery(query).executeUpdate();
    }
}
