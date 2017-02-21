package com.kwery.tests.services;

import com.google.common.collect.ImmutableList;
import com.kwery.services.scheduler.CsvToHtmlConverter;
import com.kwery.tests.util.TestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CsvToHtmlTableConverterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception {
        File file = testFolder.newFile();

        List<String[]> datum = ImmutableList.of(
                new String[]{"header0", "header1"},
                new String[]{"value0", "value1"}
        );

        TestUtil.writeCsv(datum, file);

        String expectedHtmlTable = String.join("",
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

        CsvToHtmlConverter converter = new CsvToHtmlConverter(file);

        assertThat(converter.convert(), is(expectedHtmlTable));
        assertThat(converter.isHasContent(), is(true));
    }

    @Test
    public void testOnlyHeaders() throws Exception {
        File file = testFolder.newFile();

        List<String[]> datum = ImmutableList.of(
                new String[]{"header0", "header1"}
        );

        TestUtil.writeCsv(datum, file);

        String expectedHtmlTable = String.join("",
                ImmutableList.of(
                        "<table style='border: 1px solid black; table-layout: auto;'>",
                        "<tr style='border: 1px solid black;'>",
                        "<th style='border: 1px solid black;'>", "header0", "</th>",
                        "<th style='border: 1px solid black;'>", "header1", "</th>",
                        "</tr>",
                        "</table>"
                )
        );

        CsvToHtmlConverter converter = new CsvToHtmlConverter(file);

        assertThat(converter.convert(), is(expectedHtmlTable));
        assertThat(converter.isHasContent(), is(false));
    }

    @Test
    public void testEmptyResult() throws Exception {
        File file = testFolder.newFile();

        String expectedHtmlTable = String.join("",
                ImmutableList.of(
                        "<table style='border: 1px solid black; table-layout: auto;'>",
                        "</table>"
                )
        );

        CsvToHtmlConverter converter = new CsvToHtmlConverter(file);

        assertThat(converter.convert(), is(expectedHtmlTable));
        assertThat(converter.isHasContent(), is(false));
    }
}
