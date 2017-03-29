package com.kwery.services.mail.converter;

import au.com.bytecode.opencsv.CSVReader;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.dtos.email.ReportEmailSection;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvToReportEmailSectionConverter {
    protected final File csvFile;

    @Inject
    public CsvToReportEmailSectionConverter(@Assisted File csvFile) {
        this.csvFile = csvFile;
    }

    public ReportEmailSection convert() throws IOException {
        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        List<String[]> lines = csvReader.readAll();

        ReportEmailSection section = new ReportEmailSection();

        if (!lines.isEmpty()) {
            section.setHeaders(Arrays.asList(lines.get(0)));
        }

        if (lines.size() > 1) {
            List<List<String>> rows = new ArrayList<>();

            for (String[] strings : lines.subList(1, lines.size())) {
                rows.add(Arrays.asList(strings));
            }

            section.setRows(rows);
        }

        return section;
    }
}
