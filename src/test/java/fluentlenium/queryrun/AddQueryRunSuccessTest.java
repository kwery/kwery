package fluentlenium.queryrun;

import dtos.QueryRunDto;
import org.junit.Test;

import static util.TestUtil.queryRunDto;

public class AddQueryRunSuccessTest extends QueryRunTest {
    @Test
    public void test() {
        QueryRunDto dto = queryRunDto();
        dto.setDatasourceId(datasource.getId());
        page.submitForm(dto);
        page.waitForSuccessMessage();
    }
}
