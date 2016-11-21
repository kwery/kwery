package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dtos.SqlQueryDto;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.queryRunDto;

public class AddSqlQueryWithoutDependsOnSuccessTest extends SqlQueryTest {
    @Test
    public void test() {
        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasourceId);
        page.submitForm(dto, true);
        page.waitForScheduledMessageSuccess();
    }
}
