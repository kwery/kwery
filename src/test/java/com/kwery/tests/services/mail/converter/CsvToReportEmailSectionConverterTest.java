package com.kwery.tests.services.mail.converter;

import com.google.common.collect.ImmutableList;
import com.kwery.dtos.email.ReportEmailSection;
import com.kwery.services.mail.converter.CsvToReportEmailSectionConverter;
import com.kwery.tests.util.TestUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static com.jayway.jsonassert.impl.matcher.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CsvToReportEmailSectionConverterTest {
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void test() throws Exception {
        File file = testFolder.newFile();

        List<String[]> datum = ImmutableList.of(
                new String[]{"header0", "header1"},
                new String[]{"value00", "value01"},
                new String[]{"value10", "value11"}
        );

        TestUtil.writeCsv(datum, file);

        CsvToReportEmailSectionConverter converter = new CsvToReportEmailSectionConverter(file);
        ReportEmailSection section = converter.convert();

        assertThat(section.getHeaders(), is(ImmutableList.of("header0", "header1")));
        assertThat(section.getRows().get(0), is(ImmutableList.of("value00", "value01")));
        assertThat(section.getRows().get(1), is(ImmutableList.of("value10", "value11")));
    }

    @Test
    public void testOnlyHeaders() throws Exception {
        File file = testFolder.newFile();

        List<String[]> datum = ImmutableList.of(
                new String[]{"header0", "header1"}
        );

        TestUtil.writeCsv(datum, file);

        CsvToReportEmailSectionConverter converter = new CsvToReportEmailSectionConverter(file);
        ReportEmailSection section = converter.convert();

        assertThat(section.getHeaders(), is(ImmutableList.of("header0", "header1")));
        assertThat(section.getRows(), empty());
    }

    @Test
    public void testEmptyResult() throws Exception {
        File file = testFolder.newFile();


        CsvToReportEmailSectionConverter converter = new CsvToReportEmailSectionConverter(file);
        ReportEmailSection section = converter.convert();

        assertThat(section.getHeaders(), empty());
        assertThat(section.getRows(), empty());
    }
}
