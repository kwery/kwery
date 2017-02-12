package com.kwery.services.scheduler;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import static au.com.bytecode.opencsv.CSVParser.DEFAULT_ESCAPE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

public class ResultSetToCsvWriter {
    private final ResultSet resultSet;
    private final File outputFile;

    @Inject
    public ResultSetToCsvWriter(@Assisted ResultSet resultSet, @Assisted File outputFile) {
        this.resultSet = resultSet;
        this.outputFile = outputFile;
    }

    public void write() throws SQLException, IOException {
        try (FileWriter fileWriter = new FileWriter(outputFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            CSVWriter csvWriter = new CSVWriter(printWriter, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, System.lineSeparator())) {
            csvWriter.writeAll(resultSet, true);
        }
    }
}
