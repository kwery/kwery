package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelTable;
import static com.kwery.tests.util.TestUtil.jobLabelModel;

public class JobLabelDaoUpdateParentTest extends RepoDashDaoTestBase {
    private JobLabelModel parentJobLabelModel0;
    private JobLabelModel childJobLabelModel;
    private JobLabelModel parentJobLabelModel1;
    private JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        parentJobLabelModel0 = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel0);

        childJobLabelModel = jobLabelModel();
        childJobLabelModel.setParentLabel(parentJobLabelModel0);
        jobLabelDbSetUp(childJobLabelModel);

        parentJobLabelModel1 = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel1);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() throws Exception {
        childJobLabelModel.setParentLabel(parentJobLabelModel1);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobLabelModel expected = mapper.map(childJobLabelModel, JobLabelModel.class);

        jobLabelDao.save(childJobLabelModel);

        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(parentJobLabelModel0, parentJobLabelModel1, expected)).build().assertTable();
    }
}
