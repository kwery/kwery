package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.core.Is.is;

public class JobLabelDaoDeleteParentJobTest extends RepoDashDaoTestBase {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private JobLabelModel jobLabelModel;
    private JobLabelDao jobLabelDao;
    private JobLabelModel parentJobLabelModel;

    @Before
    public void setUp() {
        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        jobLabelModel = jobLabelModel();
        jobLabelModel.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(jobLabelModel);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() {
        assertThat(jobLabelDao.isParentLabel(parentJobLabelModel.getId()), is(true));
        assertThat(jobLabelDao.isParentLabel(jobLabelModel.getId()), is(false));
        expectedException.expectCause(isA(PersistenceException.class));
        jobLabelDao.deleteJobLabelById(parentJobLabelModel.getId());
    }
}
