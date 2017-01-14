package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.util.TestUtil.jobLabelModel;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.junit.Assert.assertThat;

public class JobLabelDaoQueryTest extends RepoDashDaoTestBase {
    private JobLabelDao jobLabelDao;
    private JobLabelModel parentJobLabelModel;
    private JobLabelModel childJobLabelModel0;
    private JobLabelModel childJobLabelModel1;

    @Before
    public void setUp() {
        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        childJobLabelModel0 = jobLabelModel();
        childJobLabelModel0.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(childJobLabelModel0);

        childJobLabelModel1 = jobLabelModel();
        childJobLabelModel1.setParentLabel(parentJobLabelModel);
        jobLabelDbSetUp(childJobLabelModel1);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void testGetById() {
        DozerBeanMapper mapper = new DozerBeanMapper();

        JobLabelModel expectedParentJobLabelModel = mapper.map(parentJobLabelModel, JobLabelModel.class);
        expectedParentJobLabelModel.getChildLabels().add(childJobLabelModel0);
        expectedParentJobLabelModel.getChildLabels().add(childJobLabelModel1);

        childJobLabelModel0.setParentLabel(expectedParentJobLabelModel);
        childJobLabelModel1.setParentLabel(expectedParentJobLabelModel);

        assertThat(jobLabelDao.getJobLabelModelById(parentJobLabelModel.getId()), theSameBeanAs(expectedParentJobLabelModel));
    }

    @Test
    public void testGetByLabel() {
        DozerBeanMapper mapper = new DozerBeanMapper();

        JobLabelModel expectedParentJobLabelModel = mapper.map(parentJobLabelModel, JobLabelModel.class);
        expectedParentJobLabelModel.getChildLabels().add(childJobLabelModel0);
        expectedParentJobLabelModel.getChildLabels().add(childJobLabelModel1);
        childJobLabelModel0.setParentLabel(expectedParentJobLabelModel);
        childJobLabelModel1.setParentLabel(expectedParentJobLabelModel);

        assertThat(jobLabelDao.getJobLabelModelByLabel(parentJobLabelModel.getLabel()), theSameBeanAs(expectedParentJobLabelModel));
    }
}
