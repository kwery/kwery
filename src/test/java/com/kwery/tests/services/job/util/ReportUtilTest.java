package com.kwery.tests.services.job.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.kwery.models.JobExecutionModel;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.utils.ReportUtil;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportUtilTest {
    @Test
    public void test() {
        JobModel jobModel = new JobModel();

        SqlQueryModel sqlQueryModel0 = new SqlQueryModel();
        sqlQueryModel0.setId(0);

        SqlQueryModel sqlQueryModel1 = new SqlQueryModel();
        sqlQueryModel1.setId(1);

        SqlQueryModel sqlQueryModel2 = new SqlQueryModel();
        sqlQueryModel2.setId(2);

        jobModel.setSqlQueries(ImmutableList.of(sqlQueryModel0, sqlQueryModel1, sqlQueryModel2));

        JobExecutionModel jobExecutionModel = new JobExecutionModel();
        jobExecutionModel.setJobModel(jobModel);

        SqlQueryExecutionModel sqlQueryExecutionModel0 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel0.setId(100);
        sqlQueryExecutionModel0.setSqlQuery(sqlQueryModel0);

        SqlQueryExecutionModel sqlQueryExecutionModel1 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel1.setId(1);
        sqlQueryExecutionModel1.setSqlQuery(sqlQueryModel1);

        SqlQueryExecutionModel sqlQueryExecutionModel2 = new SqlQueryExecutionModel();
        sqlQueryExecutionModel2.setId(20);
        sqlQueryExecutionModel2.setSqlQuery(sqlQueryModel2);

        jobExecutionModel.setSqlQueryExecutionModels(ImmutableSet.of(sqlQueryExecutionModel0, sqlQueryExecutionModel1, sqlQueryExecutionModel2));

        List<SqlQueryExecutionModel> orderedExecutions = ReportUtil.orderedExecutions(jobExecutionModel);

        assertThat(orderedExecutions.get(0).getId(), is(100));
        assertThat(orderedExecutions.get(1).getId(), is(1));
        assertThat(orderedExecutions.get(2).getId(), is(20));
    }
}
