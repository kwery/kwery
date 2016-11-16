package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.dtos.SqlQueryDto;
import org.junit.Test;

import static com.kwery.tests.util.TestUtil.queryRunDto;

public class AddSqlQueryDuplicateLabelTest extends AddSqlQuerySuccessTest {
    @Test
    public void test() {
        super.test();

        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasource.getId());

        page.submitForm(dto);
        page.waitForDuplicateLabelMessage(dto.getLabel());
    }
}
