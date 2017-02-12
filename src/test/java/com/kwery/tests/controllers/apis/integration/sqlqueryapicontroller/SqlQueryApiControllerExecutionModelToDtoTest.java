package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.dtos.SqlQueryExecutionDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import org.junit.Test;

import java.util.Date;

import static com.kwery.models.SqlQueryExecutionModel.Status.SUCCESS;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerExecutionModelToDtoTest {
    @Test
    public void test() {
        Datasource datasource = new Datasource();
        datasource.setLabel("testLabel");

        SqlQueryModel sqlQuery = new SqlQueryModel();
        sqlQuery.setId(1);
        sqlQuery.setLabel("testSqlQuery");
        sqlQuery.setDatasource(datasource);
        sqlQuery.setQuery("select * from foo");

        SqlQueryExecutionModel sqlQueryExecution = new SqlQueryExecutionModel();
        sqlQueryExecution.setSqlQuery(sqlQuery);
        sqlQueryExecution.setExecutionStart(1475215333445l);
        sqlQueryExecution.setExecutionEnd(1475215453445l);
        sqlQueryExecution.setExecutionId("executionId");
        sqlQueryExecution.setStatus(SUCCESS);
        sqlQueryExecution.setExecutionError("success");

        SqlQueryApiController controller = new SqlQueryApiController();
        SqlQueryExecutionDto dto = controller.from(sqlQueryExecution);

        assertThat(dto.getSqlQueryLabel(), is(sqlQuery.getLabel()));
        assertThat(dto.getSqlQueryExecutionStartTime(), is("Fri Sep 30 2016 11:32"));
        assertThat(dto.getSqlQueryExecutionEndTime(), is("Fri Sep 30 2016 11:34"));
        assertThat(dto.getDatasourceLabel(), is(datasource.getLabel()));
        assertThat(dto.getSqlQueryExecutionId(), is(sqlQueryExecution.getExecutionId()));
        assertThat(dto.getSqlQueryId(), is(sqlQuery.getId()));
        assertThat(dto.getStatus(), is(SUCCESS.name()));
        assertThat(dto.getResult(), is("success"));
    }

    public static void main(String[] args) {
        Date date = new Date();
        date.setTime(1475215333445l);
        date.setMinutes(34);
        System.out.println(date.getTime());
        System.out.println(new Date(date.getTime()));
    }
}
