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
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.kwery.tests.util.TestUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

public class SearchDaoTestFieldPrecedence extends RepoDashDaoTestBase {
    private JobSearchDao jobSearchDao;
    private JobModel inTitle;
    private JobModel inName;
    private JobModel inJobLabel;
    private JobModel inSqlQueryTitle;
    private JobModel inSqlQueryLabel;
    private JobModel inDatasource;

    @Before
    public void setUp() {
        JobDao jobDao = getInstance(JobDao.class);
        JobLabelDao jobLabelDao = getInstance(JobLabelDao.class);
        DatasourceDao datasourceDao = getInstance(DatasourceDao.class);

        //In title
        inTitle = jobModelWithoutIdWithoutDependents();
        inTitle.setTitle("foo moo");
        jobDao.save(inTitle);

        //In name
        inName = jobModelWithoutIdWithoutDependents();
        inName.setName("foo");
        jobDao.save(inName);

        //In label
        JobLabelModel jobLabelModel = jobLabelModelWithoutId();
        jobLabelModel.setLabel("foo");
        jobLabelDao.save(jobLabelModel);

        inJobLabel = jobModelWithoutIdWithoutDependents();
        inJobLabel.getLabels().add(jobLabelModel);
        jobDao.save(inJobLabel);

        Datasource datasource = datasourceWithoutId();
        datasourceDao.save(datasource);

        //In SQL query title
        SqlQueryModel sqlQueryModel0 = sqlQueryModelWithoutId(datasource);
        sqlQueryModel0.setTitle("foo");
        inSqlQueryTitle = jobModelWithoutIdWithoutDependents();
        inSqlQueryTitle.getSqlQueries().add(sqlQueryModel0);
        jobDao.save(inSqlQueryTitle);

        //In SQL query label
        SqlQueryModel sqlQueryModel1 = sqlQueryModelWithoutId(datasource);
        sqlQueryModel1.setLabel("foo");
        inSqlQueryLabel = jobModelWithoutIdWithoutDependents();
        inSqlQueryLabel.getSqlQueries().add(sqlQueryModel1);
        jobDao.save(inSqlQueryLabel);

        //In datasource
        Datasource datasource1 = datasourceWithoutId();
        datasource1.setLabel("foo");
        datasourceDao.save(datasource1);

        SqlQueryModel sqlQueryModel2 = sqlQueryModelWithoutId(datasource1);
        inDatasource = jobModelWithoutIdWithoutDependents();
        inDatasource.getSqlQueries().add(sqlQueryModel2);
        jobDao.save(inDatasource);

        jobSearchDao = getInstance(JobSearchDao.class);
    }

    @Test
    public void test() {
        SearchFilter searchFilter = new SearchFilter();
        searchFilter.setPhrase("foo");
        searchFilter.setMaxResults(1);

        searchFilter.setFirstResult(0);
        List result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(inTitle.getId()));

        searchFilter.setFirstResult(1);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(inName.getId()));

        searchFilter.setFirstResult(2);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), anyOf(equalTo(inJobLabel.getId()), equalTo(inSqlQueryTitle.getId()), equalTo(inSqlQueryLabel.getId())));

        searchFilter.setFirstResult(3);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), anyOf(equalTo(inJobLabel.getId()), equalTo(inSqlQueryTitle.getId()), equalTo(inSqlQueryLabel.getId())));

        searchFilter.setFirstResult(4);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), anyOf(equalTo(inJobLabel.getId()), equalTo(inSqlQueryTitle.getId()), equalTo(inSqlQueryLabel.getId())));

        searchFilter.setFirstResult(5);
        result = jobSearchDao.search(searchFilter);
        assertThat(result.size(), is(1));
        assertThat(((JobModel)result.get(0)).getId(), is(inDatasource.getId()));
    }
}
