package com.kwery.tests.dao.jobdao.save;

import com.google.common.base.Strings;
import com.kwery.models.Datasource;
import com.kwery.models.JobModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.util.RepoDashDaoTestBase;
import org.junit.Test;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.util.TestUtil.*;

public class JobDaoSaveSqlQueryWithMaxQueryLengthTest extends RepoDashDaoTestBase {
    @Test
    public void test() {
        JobModel jobModel = jobModelWithoutDependents();
        jobModel.setParameterCsv("foo bar moo");

        jobDbSetUp(jobModel);

        Datasource datasource = datasource();
        datasourceDbSetup(datasource);

        SqlQueryModel sqlQueryModel = sqlQueryModel(datasource);
        sqlQueryModel.setQuery(Strings.repeat("x", SqlQueryModel.QUERY_MAX_LENGTH));
        sqlQueryDbSetUp(sqlQueryModel);
    }
}
