package com.kwery.tests.services.job.parameterised;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.kwery.services.job.parameterised.ParameterCsvExtractor;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class ParameterCsvExtractorTest {
    @Test
    public void testParse() {
        ParameterCsvExtractor manipulator = new ParameterCsvExtractor();

        assertThat(manipulator.parse(String.valueOf(Byte.MIN_VALUE)), instanceOf(Byte.class));
        assertThat(manipulator.parse(String.valueOf(Byte.MAX_VALUE)), instanceOf(Byte.class));

        assertThat(manipulator.parse(String.valueOf(Short.MIN_VALUE)), instanceOf(Short.class));
        assertThat(manipulator.parse(String.valueOf(Short.MAX_VALUE)), instanceOf(Short.class));

        assertThat(manipulator.parse(String.valueOf(Integer.MIN_VALUE)), instanceOf(Integer.class));
        assertThat(manipulator.parse(String.valueOf(Integer.MAX_VALUE)), instanceOf(Integer.class));

        assertThat(manipulator.parse(String.valueOf(Long.MIN_VALUE)), instanceOf(Long.class));
        assertThat(manipulator.parse(String.valueOf(Long.MAX_VALUE)), instanceOf(Long.class));

        assertThat(manipulator.parse(String.valueOf(Float.MIN_VALUE)), instanceOf(Float.class));
        assertThat(manipulator.parse(String.valueOf(Float.MAX_VALUE)), instanceOf(Float.class));

        //TODO - Fix this
/*        System.out.println("Double min value - " + String.valueOf(Double.MIN_VALUE));
        System.out.println("Double max value - " + String.valueOf(Double.MAX_VALUE));

        assertThat(manipulator.parse(String.valueOf(Double.MIN_VALUE)), instanceOf(Double.class));
        assertThat(manipulator.parse(String.valueOf(Double.MAX_VALUE)), instanceOf(Double.class));*/
    }

    @Test
    public void testExtract() throws IOException {
        StringWriter stringWriter = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            List<String[]> csv = ImmutableList.of(
                    "zero, one, two".split(","),
                    "1, 1.1, foo".split(","),
                    "2, 2.2, bar".split(",")
            );
            csvWriter.writeAll(csv);
        }

        ParameterCsvExtractor extractor = new ParameterCsvExtractor();

        List<Map<String, ?>> parameters = extractor.extract(stringWriter.toString());

        assertThat(parameters.size(), is(2));

        for (Map<String, ?> parameter : parameters) {
            assertThat(parameter.containsKey("zero"), is(true));
            assertThat(parameter.containsKey("one"), is(true));
            assertThat(parameter.containsKey("two"), is(true));
        }

        assertThat(parameters.get(0).get("zero"), equalTo(new Byte("1")));
        assertThat(parameters.get(0).get("one"), equalTo(1.1F));
        assertThat(parameters.get(0).get("two"), equalTo("foo"));

        assertThat(parameters.get(1).get("zero"), equalTo(new Byte("2")));
        assertThat(parameters.get(1).get("one"), equalTo(2.2F));
        assertThat(parameters.get(1).get("two"), equalTo("bar"));
    }

    @Test
    public void testEmptyEmails() throws IOException {
        Map<String, ?> parameters = ImmutableMap.of(
                "foo", "bar",
                "moo", "goo"
        );

        assertThat(new ParameterCsvExtractor().emails(parameters).size(), is(0));
    }

    @Test
    public void testEmails() throws IOException {
        Map<String, ?> parameters = ImmutableMap.of(
                "email_csv", "purvi@getkwery.com, pavi@getkwery.com,,"
        );

        assertThat(new ParameterCsvExtractor().emails(parameters), containsInAnyOrder("purvi@getkwery.com", "pavi@getkwery.com"));
    }
}
