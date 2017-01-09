package com.kwery.tests.dao.joblabeldao;

import com.kwery.dao.JobLabelDao;
import com.kwery.models.JobLabelModel;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.apache.commons.lang3.RandomStringUtils;
import org.dbunit.DatabaseUnitException;
import org.dozer.DozerBeanMapper;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.sql.SQLException;

import static com.kwery.models.JobLabelModel.ID_COLUMN;
import static com.kwery.models.JobLabelModel.JOB_LABEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.jobLabelTable;
import static com.kwery.tests.util.TestUtil.jobLabelModel;

public class JobLabelDaoSaveTest extends RepoDashDaoTestBase {
    protected JobLabelDao jobLabelDao;

    @Before
    public void setUp() {
        jobLabelDao = getInstance(JobLabelDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        JobLabelModel m = jobLabelModel();

        DozerBeanMapper mapper = new DozerBeanMapper();
        JobLabelModel expected = mapper.map(m, JobLabelModel.class);
        jobLabelDao.save(m);

        new DbTableAsserterBuilder(JOB_LABEL_TABLE, jobLabelTable(expected)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }

    @Test(expected = ConstraintViolationException.class)
    public void testNullLabel() {
        JobLabelModel m = jobLabelModel();
        m.setLabel(null);
        jobLabelDao.save(m);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testEmptyLabel() {
        JobLabelModel m = jobLabelModel();
        m.setLabel("");
        jobLabelDao.save(m);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testLabelLength() {
        JobLabelModel m = jobLabelModel();
        m.setLabel(RandomStringUtils.randomAlphanumeric(JobLabelModel.LABEL_MAX_LENGTH + 1, 1000));
        jobLabelDao.save(m);
    }
}
