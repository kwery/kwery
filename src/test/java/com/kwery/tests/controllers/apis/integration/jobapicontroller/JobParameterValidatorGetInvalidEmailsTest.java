package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.kwery.controllers.apis.JobParameterValidator;
import com.kwery.dtos.JobDto;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class JobParameterValidatorGetInvalidEmailsTest extends RepoDashTestBase {
    protected JobParameterValidator jobParameterValidator;

    @Before
    public void setUp() {
        jobParameterValidator = getInstance(JobParameterValidator.class);
    }

    @Test
    public void test() throws IOException {
        JobDto jobDto = new JobDto();

        String parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                        "header0, kwery_email",
                        "value0, \"abhi@,foo,abhi@getkwery.com, \"\"abhirama\"\" <abhi@getkwery.com>\""
                )
        );
        jobDto.setParameterCsv(parameterCsv);

        assertThat(jobParameterValidator.getInvalidEmails(jobDto), containsInAnyOrder("foo", "abhi@"));
    }
}
