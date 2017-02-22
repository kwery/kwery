package com.kwery.tests.dao.search;

import com.kwery.dao.DatasourceDao;
import com.kwery.dao.JobDao;
import com.kwery.dao.JobLabelDao;
import com.kwery.dao.search.JobSearchDao;
import com.kwery.dao.search.SearchFilter;
import com.kwery.models.Datasource;
import com.kwery.models.JobLabelModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobSearchDaoTest extends RepoDashDaoTestBase {
    private JobSearchDao jobSearchDao;
    private JobModel jobModel0;
    private JobModel jobModel1;

    @Before
    public void setUp() {
        JobDao jobDao = getInstance(JobDao.class);
        JobLabelDao jobLabelDao = getInstance(JobLabelDao.class);
        DatasourceDao datasourceDao = getInstance(DatasourceDao.class);

        jobModel0 = TestUtil.jobModelWithoutIdWithoutDependents();
        jobModel0.setTitle("foo moo");
        jobDao.save(jobModel0);

        jobModel1 = TestUtil.jobModelWithoutIdWithoutDependents();
        jobModel1.setTitle("foo bar roo");
        jobDao.save(jobModel1);

        JobModel jobModel2 = TestUtil.jobModelWithoutIdWithoutDependents();
        jobModel2.setTitle("sljfsdj");
        jobModel2.setName("cho");
        jobDao.save(jobModel2);

        JobLabelModel jobLabelModel = TestUtil.jobLabelModelWithoutId();
        jobLabelModel.setLabel("bago");
        jobLabelDao.save(jobLabelModel);

        JobModel jobModel3 = TestUtil.jobModelWithoutIdWithoutDependents();
        jobModel3.getLabels().add(jobLabelModel);
        jobDao.save(jobModel3);

        Datasource datasource = TestUtil.datasourceWithoutId();
        datasource.setLabel("mysql");
        datasourceDao.save(datasource);

        SqlQueryModel sqlQueryModel = TestUtil.sqlQueryModelWithoutId(datasource);
        sqlQueryModel.setLabel("sql");
        sqlQueryModel.setTitle("sqlQueryTitle");
        JobModel jobModel4 = TestUtil.jobModelWithoutIdWithoutDependents();
        jobModel4.getSqlQueries().add(sqlQueryModel);
        jobDao.save(jobModel4);

        jobSearchDao = getInstance(JobSearchDao.class);
    }

    @Test
    public void testFound() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase("foo");
        searchFilter.setFirstResult(0);
        searchFilter.setMaxResults(1);

        List result = jobSearchDao.search(searchFilter);

        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), anyOf(equalTo(jobModel0.getId()), equalTo(jobModel1.getId())));

        searchFilter.setFirstResult(1);
        searchFilter.setMaxResults(1);

        result = jobSearchDao.search(searchFilter);

        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), anyOf(equalTo(jobModel0.getId()), equalTo(jobModel1.getId())));

        searchFilter.setFirstResult(2);
        searchFilter.setMaxResults(1);

        result = jobSearchDao.search(searchFilter);

        assertThat(result.size(), is(0));
    }

    @Test
    public void testNotFound() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(UUID.randomUUID().toString());
        searchFilter.setFirstResult(0);
        searchFilter.setMaxResults(1);

        List result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(0));
    }
}
