package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.JobLabelModel.ID_COLUMN;
import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelDbSetUp;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelTable;
import static com.kwery.tests.util.TestUtil.jobLabelModel;

public class JobLabelDaoSaveChildLabelTest extends RepoDashDaoTestBase {
    private JobLabelModel parentJobLabelModel;
    JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        parentJobLabelModel = jobLabelModel();
        jobLabelDbSetUp(parentJobLabelModel);

        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() throws Exception {
        JobLabelModel childJobLabelModel = jobLabelModel();
        childJobLabelModel.setParentLabel(parentJobLabelModel);

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobLabelModel expected = mapper.map(childJobLabelModel, JobLabelModel.class);

        jobLabelDao.save(childJobLabelModel);

        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(expected, parentJobLabelModel)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }
}
