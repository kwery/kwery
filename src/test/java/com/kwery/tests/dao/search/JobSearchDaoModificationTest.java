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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobSearchDaoModificationTest extends RepoDashDaoTestBase {
    private String jobTitle;
    private String jobName;
    private String labelName;
    private String queryTitle;
    private String queryLabel;
    private String datasourceLabel;

    private JobModel datasourceLabelSearchJobModel;
    private JobModel sqlQueryLabelSearchJobModel;
    private JobModel sqlQueryTitleSearchJobModel;
    private JobModel jobLabelSearchJobModel;
    private JobModel titleSearchJobModel;
    private JobModel nameSearchJobModel;

    private JobSearchDao jobSearchDao;
    private JobDao jobDao;
    private JobLabelDao jobLabelDao;
    private DatasourceDao datasourceDao;
    private JobLabelModel jobLabelModel;
    private SqlQueryModel sqlQueryModel0;
    private SqlQueryModel sqlQueryModel1;
    private Datasource datasource1;
    private SqlQueryModel sqlQueryModel2;
    private Datasource datasource0;

    @Before
    public void setUp() {
        jobDao = getInstance(JobDao.class);
        jobLabelDao = getInstance(JobLabelDao.class);
        datasourceDao = getInstance(DatasourceDao.class);

        jobTitle = RandomStringUtils.randomAlphabetic(10);

        titleSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        titleSearchJobModel.setTitle(jobTitle);
        jobDao.save(titleSearchJobModel);

        jobName = RandomStringUtils.randomAlphabetic(10);
        nameSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        nameSearchJobModel.setName(jobName);
        jobDao.save(nameSearchJobModel);

        jobLabelModel = TestUtil.jobLabelModelWithoutId();
        labelName = RandomStringUtils.randomAlphabetic(10);
        jobLabelModel.setLabel(labelName);
        jobLabelModel = jobLabelDao.save(jobLabelModel);

        jobLabelSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        jobLabelSearchJobModel.getLabels().add(jobLabelModel);
        jobDao.save(jobLabelSearchJobModel);

        datasource0 = TestUtil.datasourceWithoutId();
        datasourceDao.save(datasource0);

        sqlQueryModel0 = TestUtil.sqlQueryModelWithoutId(datasource0);
        queryTitle = RandomStringUtils.randomAlphabetic(10);
        sqlQueryModel0.setTitle(queryTitle);
        sqlQueryTitleSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        sqlQueryTitleSearchJobModel.getSqlQueries().add(sqlQueryModel0);
        jobDao.save(sqlQueryTitleSearchJobModel);

        sqlQueryModel1 = TestUtil.sqlQueryModelWithoutId(datasource0);
        queryLabel = RandomStringUtils.randomAlphabetic(10);
        sqlQueryModel1.setLabel(queryLabel);
        sqlQueryLabelSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        sqlQueryLabelSearchJobModel.getSqlQueries().add(sqlQueryModel1);
        jobDao.save(sqlQueryLabelSearchJobModel);

        datasource1 = TestUtil.datasourceWithoutId();
        datasourceLabel = RandomStringUtils.randomAlphabetic(10);
        datasource1.setLabel(datasourceLabel);
        datasourceDao.save(datasource1);

        sqlQueryModel2 = TestUtil.sqlQueryModelWithoutId(datasource1);
        datasourceLabelSearchJobModel = TestUtil.jobModelWithoutIdWithoutDependents();
        datasourceLabelSearchJobModel.getSqlQueries().add(sqlQueryModel2);
        jobDao.save(datasourceLabelSearchJobModel);

        jobSearchDao = getInstance(JobSearchDao.class);
    }

    @Test
    public void testUpdateJobTitle() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(jobTitle);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        titleSearchJobModel.setTitle(RandomStringUtils.randomAlphabetic(3));

        jobDao.save(titleSearchJobModel);

        System.out.println("========================================================================================");

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testDeleteJobTitle() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(jobTitle);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        jobDao.delete(titleSearchJobModel.getId());

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateJobName() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(jobName);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        nameSearchJobModel.setName(RandomStringUtils.randomAlphabetic(3));
        jobDao.save(nameSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testDeleteJobName() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(jobName);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        jobDao.delete(nameSearchJobModel.getId());

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateJobLabel() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(labelName);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        jobLabelModel.setLabel(RandomStringUtils.randomAlphabetic(3));
        jobLabelDao.save(jobLabelModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testRemoveJobLabel() throws Exception {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(labelName);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        jobLabelSearchJobModel.getLabels().clear();

        jobDao.save(jobLabelSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateSqlQueryWithTitle() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(queryTitle);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        sqlQueryModel0.setTitle(RandomStringUtils.randomAlphabetic(3));
        sqlQueryTitleSearchJobModel.getSqlQueries().clear();
        sqlQueryTitleSearchJobModel.getSqlQueries().add(sqlQueryModel0);
        jobDao.save(sqlQueryTitleSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testDeleteSqlQueryWithTitle() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(queryTitle);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        sqlQueryTitleSearchJobModel.getSqlQueries().clear();

        jobDao.save(sqlQueryTitleSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateSqlQueryWithLabel() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(queryLabel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        sqlQueryModel1.setLabel(RandomStringUtils.randomAlphabetic(3));
        sqlQueryLabelSearchJobModel.getSqlQueries().clear();
        sqlQueryLabelSearchJobModel.getSqlQueries().add(sqlQueryModel1);
        jobDao.save(sqlQueryLabelSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testDeleteSqlQueryWithLabel() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(queryLabel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        sqlQueryModel1.setLabel(RandomStringUtils.randomAlphabetic(3));
        sqlQueryLabelSearchJobModel.getSqlQueries().clear();
        jobDao.save(sqlQueryLabelSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateSqlQueryWithDatasource() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(datasourceLabel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        datasource1.setLabel(RandomStringUtils.randomAlphabetic(3));
        datasourceDao.update(datasource1);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }

    @Test
    public void testUpdateToAnotherDatasourceSqlQueryWithDatasource() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase(datasourceLabel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(1));

        datasourceLabelSearchJobModel.getSqlQueries().clear();
        sqlQueryModel2.setDatasource(datasource0);
        datasourceLabelSearchJobModel.getSqlQueries().add(sqlQueryModel2);
        jobDao.save(datasourceLabelSearchJobModel);

        assertThat(jobSearchDao.search(searchFilter).size(), is(0));
    }
}
