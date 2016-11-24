package com.kwery.tests.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.kwery.services.scheduler.JsonToHtmlTable;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;

public class JsonToHtmlTableTest {
    @Test
    public void test() throws IOException {
        List<List<String>> result = ImmutableList.of(
            ImmutableList.of("header0", "header1"),
            ImmutableList.of("value0", "value1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table>",
                            "<tr>",
                                "<th>", "header0", "</th>",
                                "<th>", "header1", "</th>",
                            "</tr>",
                            "<tr>",
                                "<td>", "value0", "</td>",
                                "<td>", "value1", "</td>",
                            "</tr>",
                        "</table>"
                )
        );

        assertThat(new JsonToHtmlTable().convert(json), Is.is(expectedHtmlTable));
    }

    @Test
    public void testOnlyHeaders() throws IOException {
        List<List<String>> result = ImmutableList.of(
                ImmutableList.of("header0", "header1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table>",
                        "<tr>",
                        "<th>", "header0", "</th>",
                        "<th>", "header1", "</th>",
                        "</tr>",
                        "</table>"
                )
        );

        assertThat(new JsonToHtmlTable().convert(json), Is.is(expectedHtmlTable));
    }

    @Test
    public void testEmpytResult() throws IOException {
        List<List<String>> result = new LinkedList<>();

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table>",
                        "</table>"
                )
        );

        assertThat(new JsonToHtmlTable().convert(json), Is.is(expectedHtmlTable));
    }

    @Test(expected = JsonProcessingException.class)
    public void malformedJsonTest() throws IOException {
        new JsonToHtmlTable().convert("sdjkfljsl");
    }
}
