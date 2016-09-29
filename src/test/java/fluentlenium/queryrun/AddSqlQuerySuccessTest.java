package fluentlenium.queryrun;

import dtos.SqlQueryDto;
import org.junit.Test;

import static util.TestUtil.queryRunDto;

public class AddSqlQuerySuccessTest extends QueryRunTest {
    @Test
    public void test() {
        SqlQueryDto dto = queryRunDto();
        dto.setDatasourceId(datasource.getId());
        page.submitForm(dto);
        page.waitForSuccessMessage();
    }
}
