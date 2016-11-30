package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SqlQueryModel.CRON_EXPRESSION_COLUMN;
import static com.kwery.models.SqlQueryModel.DATASOURCE_ID_FK_COLUMN;
import static com.kwery.models.SqlQueryModel.QUERY_COLUMN;
import static com.kwery.tests.util.TestUtil.queryRunDto;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;

public class SqlQueryAddWithDependsOnSuccessUiTest extends SqlQueryAddAbstractUiTest {
    @Before
    public void setUpAddSqlQueryWithDependsOnSuccessTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SqlQueryModel.SQL_QUERY_TABLE)
                                .columns(SqlQueryModel.ID_COLUMN, CRON_EXPRESSION_COLUMN, SqlQueryModel.LABEL_COLUMN, QUERY_COLUMN, DATASOURCE_ID_FK_COLUMN)
                                .values(1, "", "selectQuery", "select User from mysql.user where User = 'root'", 1)
                                .build()
                )
        ).launch();

        page.getDriver().navigate().refresh();

        if (!page.isRendered()) {
            fail("Add query run page is not rendered");
        }
    }

    @Test
    public void test() throws InterruptedException {
        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasourceId);
        page.clickEnableDependsOnSqlQuery();
        page.waitForEnableDependsOnSqlQuery();
        page.submitForm(dto, false);
        page.waitForDependsOnQueryMessageSuccess();
    }
}
