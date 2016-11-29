package com.kwery.tests.fluentlenium.email;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class EmailConfigurationTestEmailDisabledUiTest extends EmailConfigurationEmptyPageSetUp {
    @Test
    public void test() {
        assertThat(page.isTestEmailConfigurationToFieldDisabled(), is(true));
        assertThat(page.isTestEmailConfigurationSubmitButtonDisabled(), is(true));
    }
}
