package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobLabelDaoDeleteByIdTest extends RepoDashDaoTestBase {
    private JobLabelModel jobLabelModel1;
    private JobLabelModel jobLabelModel0;
    private JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() {
        jobLabelDao.deleteJobLabelById(jobLabelModel0.getId());
        assertThat(jobLabelDao.getAllJobLabelModels(), hasSize(1));
        assertThat(jobLabelDao.getAllJobLabelModels().get(0), theSameBeanAs(jobLabelModel1));
    }
}
