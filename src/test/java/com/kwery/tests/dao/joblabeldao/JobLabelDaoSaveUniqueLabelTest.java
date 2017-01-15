package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;

public class JobLabelDaoSaveUniqueLabelTest extends RepoDashDaoTestBase {
    private JobLabelDao jobLabelDao;
    private JobLabelModel savedJobLabelModel;

    @Before
    public void setUp() {
        savedJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(savedJobLabelModel);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test(expected = PersistenceException.class)
    public void test() {
        JobLabelModel jobLabelModel = TestUtil.jobLabelModel();
        jobLabelModel.setLabel(savedJobLabelModel.getLabel());
        jobLabelDao.save(jobLabelModel);
    }
}
