package com.kwery.tests.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.kwery.services.scheduler.JsonToHtmlTableConvertor;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertThat;

public class JsonToHtmlTableConvertorTest {
    @Test
    public void test() throws IOException {
        List<List<String>> result = ImmutableList.of(
            ImmutableList.of("header0", "header1"),
            ImmutableList.of("value0", "value1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; width: 100%;'>",
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

        assertThat(new JsonToHtmlTableConvertor().convert(json), Is.is(expectedHtmlTable));
    }

    @Test
    public void testOnlyHeaders() throws IOException {
        List<List<String>> result = ImmutableList.of(
                ImmutableList.of("header0", "header1")
        );

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; width: 100%;'>",
                        "<tr style='border: 1px solid black;'>",
                        "<th style='border: 1px solid black;'>", "header0", "</th>",
                        "<th style='border: 1px solid black;'>", "header1", "</th>",
                        "</tr>",
                        "</table>"
                )
        );

        assertThat(new JsonToHtmlTableConvertor().convert(json), Is.is(expectedHtmlTable));
    }

    @Test
    public void testEmpytResult() throws IOException {
        List<List<String>> result = new LinkedList<>();

        String json = new ObjectMapper().writeValueAsString(result);

        String expectedHtmlTable = Joiner.on("").join(
                ImmutableList.of(
                        "<table style='border: 1px solid black; width: 100%;'>",
                        "</table>"
                )
        );

        assertThat(new JsonToHtmlTableConvertor().convert(json), Is.is(expectedHtmlTable));
    }

    @Test(expected = JsonProcessingException.class)
    public void malformedJsonTest() throws IOException {
        new JsonToHtmlTableConvertor().convert("sdjkfljsl");
    }
}
