package com.kwery.tests.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.kwery.services.scheduler.JsonToHtmlTableConverter;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonToHtmlTableConverterTest {
    @Test
    public void test() throws IOException {
        List<List<String>> result = ImmutableList.of(
            ImmutableList.of("header0", "header1"),
            ImmutableList.of("value0", "value1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; table-layout: auto;'>",
                            "<tr style='border: 1px solid black;'>",
                                "<th style='border: 1px solid black;'>", "header0", "</th>",
                                "<th style='border: 1px solid black;'>", "header1", "</th>",
                            "</tr>",
                            "<tr style='border: 1px solid black;'>",
                                "<td style='border: 1px solid black;'>", "value0", "</td>",
                                "<td style='border: 1px solid black;'>", "value1", "</td>",
                            "</tr>",
                        "</table>"
                )
        );

        JsonToHtmlTableConverter jsonToHtmlTableConverter = new JsonToHtmlTableConverter(json);
        assertThat(jsonToHtmlTableConverter.convert(), is(expectedHtmlTable));
        assertThat(jsonToHtmlTableConverter.isHasContent(), is(true));
    }

    @Test
    public void testOnlyHeaders() throws IOException {
        List<List<String>> result = ImmutableList.of(
                ImmutableList.of("header0", "header1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; table-layout: auto;'>",
                        "<tr style='border: 1px solid black;'>",
                        "<th style='border: 1px solid black;'>", "header0", "</th>",
                        "<th style='border: 1px solid black;'>", "header1", "</th>",
                        "</tr>",
                        "</table>"
                )
        );

        JsonToHtmlTableConverter jsonToHtmlTableConverter = new JsonToHtmlTableConverter(json);
        assertThat(jsonToHtmlTableConverter.convert(), is(expectedHtmlTable));
        assertThat(jsonToHtmlTableConverter.isHasContent(), is(false));
    }

    @Test
    public void testEmpytResult() throws IOException {
        List<List<String>> result = new LinkedList<>();

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; table-layout: auto;'>",
                        "</table>"
                )
        );

        JsonToHtmlTableConverter jsonToHtmlTableConverter = new JsonToHtmlTableConverter(json);
        assertThat(jsonToHtmlTableConverter.convert(), is(expectedHtmlTable));
        assertThat(jsonToHtmlTableConverter.isHasContent(), is(false));
    }

    @Test(expected = JsonProcessingException.class)
    public void malformedJsonTest() throws IOException {
        new JsonToHtmlTableConverter("sdjkfljsl").convert();
    }
}
