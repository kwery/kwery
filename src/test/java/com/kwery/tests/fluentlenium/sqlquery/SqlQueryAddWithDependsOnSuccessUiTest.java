package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.SqlQuery;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.SqlQuery.COLUMN_CRON_EXPRESSION;
import static com.kwery.models.SqlQuery.COLUMN_DATASOURCE_ID_FK;
import static com.kwery.models.SqlQuery.COLUMN_QUERY;
import static com.kwery.tests.util.TestUtil.queryRunDto;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static junit.framework.TestCase.fail;

public class SqlQueryAddWithDependsOnSuccessUiTest extends SqlQueryAddAbstractUiTest {
    @Before
    public void setUpAddSqlQueryWithDependsOnSuccessTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SqlQuery.TABLE)
                                .columns(SqlQuery.COLUMN_ID, COLUMN_CRON_EXPRESSION, SqlQuery.COLUMN_LABEL, COLUMN_QUERY, COLUMN_DATASOURCE_ID_FK)
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
