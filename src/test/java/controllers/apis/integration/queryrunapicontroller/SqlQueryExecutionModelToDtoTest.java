package controllers.apis.integration.queryrunapicontroller;

import controllers.apis.SqlQueryApiController;
import dtos.SqlQueryExecutionDto;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryExecutionModelToDtoTest {
    @Test
    public void test() {
        Datasource datasource = new Datasource();
        datasource.setLabel("testLabel");

        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setLabel("testSqlQuery");
        sqlQuery.setDatasource(datasource);

        SqlQueryExecution sqlQueryExecution = new SqlQueryExecution();
        sqlQueryExecution.setSqlQuery(sqlQuery);
        sqlQueryExecution.setExecutionStart(1475215333445l);

        SqlQueryApiController controller = new SqlQueryApiController();
        SqlQueryExecutionDto dto = controller.from(sqlQueryExecution);

        assertThat(dto.getSqlQueryLabel(), is(sqlQuery.getLabel()));
        assertThat(dto.getSqlQueryExecutionStartTime(), is("Fri Sep 30 2016 11:32"));
        assertThat(dto.getDatasourceLabel(), is(datasource.getLabel()));
    }
}
