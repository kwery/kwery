package fluentlenium.sqlquery;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DeleteSqlQueryPageTest extends ListSqlQueriesPageTest {
    @Test
    public void test() {
        page.waitForRows(2);
        page.delete(0);
        page.waitForDeleteSuccessMessage("testQuery0");
        List<List<String>> rows = page.rows();
        assertThat(rows, hasSize(1));
        assertThat(rows.get(0).get(0), is("testQuery1"));
    }
}
