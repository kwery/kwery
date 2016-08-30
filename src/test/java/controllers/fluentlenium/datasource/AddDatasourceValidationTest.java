package controllers.fluentlenium.datasource;

import org.junit.Test;

import static controllers.util.Messages.LABEL_VALIDATION_M;
import static controllers.util.Messages.URL_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AddDatasourceValidationTest extends DatasourceTest {
    @Test
    public void test() {
        initPage();
        page.submitForm();
        assertThat(page.usernameValidationErrorMessage(), is(USERNAME_VALIDATION_M));
        assertThat(page.urlValidationErrorMessage(), is(URL_VALIDATION_M));
        assertThat(page.labelValidationErrorMessage(), is(LABEL_VALIDATION_M));
    }
}
