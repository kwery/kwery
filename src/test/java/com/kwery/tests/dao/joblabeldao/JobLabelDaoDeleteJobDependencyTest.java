package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static com.kwery.tests.util.TestUtil.jobModelWithoutDependents;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

public class JobLabelDaoDeleteJobDependencyTest extends RepoDashDaoTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JobLabelDao jobLabelDao;
    private JobLabelModel jobLabelModel;

    @Before
    public void setUp() {
        JobModel jobModel = jobModelWithoutDependents();
        jobDbSetUp(jobModel);

        jobLabelModel = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel);

        jobModel.getLabels().add(jobLabelModel);
        jobJobLabelDbSetUp(jobModel);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() {
        assertThat(jobLabelDao.doJobsDependOnLabel(jobLabelModel.getId()), is(true));
        expectedException.expectCause(isA(PersistenceException.class));
        jobLabelDao.deleteJobLabelById(jobLabelModel.getId());
    }
}
