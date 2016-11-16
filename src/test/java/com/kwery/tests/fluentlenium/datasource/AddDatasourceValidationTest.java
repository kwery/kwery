package com.kwery.tests.fluentlenium.datasource;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static com.kwery.tests.util.Messages.LABEL_VALIDATION_M;
import static com.kwery.tests.util.Messages.PORT_VALIDATION_M;
import static com.kwery.tests.util.Messages.URL_VALIDATION_M;
import static com.kwery.tests.util.Messages.USERNAME_VALIDATION_M;

public class AddDatasourceValidationTest extends DatasourceTest {
    @Test
    public void testEmptyValues() {
        page.submitForm("", "", "", "", "");
        assertThat(page.usernameValidationErrorMessage(), is(USERNAME_VALIDATION_M));
        assertThat(page.urlValidationErrorMessage(), is(URL_VALIDATION_M));
        assertThat(page.labelValidationErrorMessage(), is(LABEL_VALIDATION_M));
        assertThat(page.portValidationErrorMessage(), is(PORT_VALIDATION_M));
    }

    @Test
    public void testPortMinimumValue() {
        page.submitForm(datasource.getUrl(), String.valueOf(0), datasource.getUsername(), datasource.getPassword(), datasource.getLabel());
        assertThat(page.portValidationErrorMessage(), is(PORT_VALIDATION_M));
    }
}
