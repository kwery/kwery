package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.kwery.controllers.apis.JobParameterValidator;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class JobParameterValidatorToCsvTest extends RepoDashTestBase {
    protected JobParameterValidator jobParameterValidator;

    @Before
    public void setUp() {
        jobParameterValidator = getInstance(JobParameterValidator.class);
    }

    @Test
    public void test() throws IOException {
        String str = "header0, header1\nvalue0, value1";
        List<List<String>> csv = jobParameterValidator.toCsv(str);
        assertThat(csv, hasSize(2));
        assertThat(csv.get(0), contains("header0", "header1"));
        assertThat(csv.get(1), contains("value0", "value1"));
    }

    @Test
    public void testWithSpacesAndQuotes() throws IOException {
        String str = "\"header0\", header1\nvalue 'value', \"\"value\"\"";
        List<List<String>> csv = jobParameterValidator.toCsv(str);
        assertThat(csv, hasSize(2));
        assertThat(csv.get(0), contains("header0", "header1"));
        assertThat(csv.get(1), contains("value 'value'", "\"value\""));
    }

    @Test
    public void testCommandEnding() throws IOException {
        String str = "header0,\nvalue0, value1";
        List<List<String>> csv = jobParameterValidator.toCsv(str);
        assertThat(csv, hasSize(2));
        assertThat(csv.get(0), contains("header0", ""));
        assertThat(csv.get(1), contains("value0", "value1"));
    }

    @Test
    public void testUneven() throws IOException {
        String str = "header0\nvalue0, value1";
        List<List<String>> csv = jobParameterValidator.toCsv(str);
        assertThat(csv, hasSize(2));
        assertThat(csv.get(0), contains("header0"));
        assertThat(csv.get(1), contains("value0", "value1"));
    }
}
