package com.kwery.services.scheduler;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CsvToHtmlConverter {
    protected final File csvFile;

    @Inject
    public CsvToHtmlConverter(@Assisted File csvFile) {
        this.csvFile = csvFile;
    }

    protected boolean hasContent = false;

    protected boolean isConvertCalled = false;

    public String convert() throws IOException {
        isConvertCalled = true;

        FileReader fileReader = new FileReader(csvFile);
        CSVReader csvReader = new CSVReader(fileReader);
        List<String[]> lines = csvReader.readAll();

        if (lines.size() > 1) {
            hasContent = true;
        }

        List<String> htmlTableParts = new LinkedList<>();
        htmlTableParts.add("<table style='border: 1px solid black; table-layout: auto;'>");

        //Has headers?
        if (!lines.isEmpty()) {
            String[] headers = lines.get(0);

            List<String> ths = new ArrayList<>(headers.length);

            htmlTableParts.add("<tr style='border: 1px solid black;'>");

            for (String header : headers) {
                String htmlHeader = "<th style='border: 1px solid black;'>" + header + "</th>";
                ths.add(htmlHeader);
            }

            htmlTableParts.add(Joiner.on("").join(ths));

            htmlTableParts.add("</tr>");

            //Has data?

            if (lines.size() > 1) {
                //Skip headers
                for (int i = 1; i < lines.size(); ++i) {
                    htmlTableParts.add("<tr style='border: 1px solid black;'>");

                    String[] row = lines.get(i);

                    List<String> tds = new ArrayList<>(row.length);
                    for (String s : row) {
                        tds.add("<td style='border: 1px solid black;'>" + s + "</td>");
                    }

                    htmlTableParts.add(String.join("", tds));

                    htmlTableParts.add("</tr>");
                }
            }
        }

        htmlTableParts.add("</table>");

        return Joiner.on("").join(htmlTableParts);
    }

    public boolean isHasContent() {
        Preconditions.checkState(isConvertCalled, "isHasContent cannot be invoked before calling convert");
        return hasContent;
    }
}
