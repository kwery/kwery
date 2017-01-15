package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobLabelDaoGetAllLabelsTest extends RepoDashDaoTestBase {
    private JobLabelModel jobLabelModel0;
    private JobLabelModel jobLabelModel1;
    private JobLabelDao jobLabelDao;

    Map<String, JobLabelModel> modelMap = new HashMap<>();

    @Before
    public void setUp() {
        jobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel0);

        jobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(jobLabelModel1);

        modelMap.put(jobLabelModel0.getLabel(), jobLabelModel0);
        modelMap.put(jobLabelModel1.getLabel(), jobLabelModel1);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() {
        for (JobLabelModel jobLabelModel : jobLabelDao.getAllJobLabelModels()) {
            assertThat(jobLabelModel, theSameBeanAs(modelMap.get(jobLabelModel.getLabel())));
        }
    }
}
