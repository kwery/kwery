package fluentlenium.queryrun;

import dtos.QueryRunDto;
import org.junit.Test;

import static util.TestUtil.queryRunDto;

public class AddQueryRunDuplicateLabelTest extends AddQueryRunSuccessTest {
    @Test
    public void test() {
        super.test();

        QueryRunDto dto = queryRunDto();
        dto.setDatasourceId(datasource.getId());

        page.submitForm(dto);
        page.waitForDuplicateLabelMessage(dto.getLabel());
    }
}
