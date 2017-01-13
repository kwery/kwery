package com.kwery.tests.dao.jobdao;

import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobModel.JOB_JOB_LABEL_TABLE;
import static com.kwery.models.JobModel.JOB_JOB_LABEL_TABLE_ID_COLUMN;
import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;

public class JobDaoUpdateLabelTest extends RepoDashDaoTestBase {
    private JobDao jobDao;
    private JobModel jobModel;
    private JobLabelModel jobLabelModel1;
    private JobLabelModel jobLabelModel2;

    @Before
    public void setUp() {
        JobLabelModel jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobLabelModel2 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel2);

        jobModel = jobModelWithoutDependents();
        jobModel.getLabels().addAll(ImmutableSet.of(jobLabelModel0, jobLabelModel2));
        jobDbSetUp(jobModel);

        jobDao = getInstance(JobDao.class);
    }

    @Test
    public void testClearLabel() throws Exception {
        jobModel.getLabels().clear();

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobDao.save(jobModel);

        new DbTableAsserterBuilder(JOB_JOB_LABEL_TABLE, jobJobLabelTable(expected)).columnToIgnore(JOB_JOB_LABEL_TABLE_ID_COLUMN).build().assertTable();
    }

    @Test
    public void testAddAndDeleteLabel() throws Exception {
        jobModel.getLabels().remove(jobLabelModel1);
        jobModel.getLabels().add(jobLabelModel2);

        JobModel expected = new DozerBeanMapper().map(jobModel, JobModel.class);
        jobDao.save(jobModel);

        new DbTableAsserterBuilder(JOB_JOB_LABEL_TABLE, jobJobLabelTable(expected)).columnToIgnore(JOB_JOB_LABEL_TABLE_ID_COLUMN).build().assertTable();
    }
}
