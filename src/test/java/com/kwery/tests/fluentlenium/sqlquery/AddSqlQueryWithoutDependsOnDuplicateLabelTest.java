package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dtos.SqlQueryDto;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.queryRunDto;

public class AddSqlQueryWithoutDependsOnDuplicateLabelTest extends AddSqlQueryWithoutDependsOnSuccessTest {
    @Test
    public void test() {
        super.test();

        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasourceId);

        page.submitForm(dto, true);
        page.waitForDuplicateLabelMessage(dto.getLabel());
    }
}
