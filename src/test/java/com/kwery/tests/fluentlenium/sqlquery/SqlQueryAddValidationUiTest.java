package com.kwery.tests.fluentlenium.sqlquery;

import org.junit.Test;

import static com.kwery.tests.util.Messages.LABEL_VALIDATION_M;
import static com.kwery.tests.util.Messages.QUERY_VALIDATION_M;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class SqlQueryAddValidationUiTest extends SqlQueryAbstractTest {
    @Test
    public void test() {
        page.submitForm();
        assertThat(page.validationMessages(), containsInAnyOrder(QUERY_VALIDATION_M, LABEL_VALIDATION_M));
    }
}
