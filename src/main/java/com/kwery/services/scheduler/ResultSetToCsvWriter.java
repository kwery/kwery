package com.kwery.services.scheduler;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.kwery.utils.CsvWriterFactory;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetToCsvWriter {
    private final ResultSet resultSet;
    private final File outputFile;
    private final CsvWriterFactory csvWriterFactory;

    @Inject
    public ResultSetToCsvWriter(CsvWriterFactory csvWriterFactory, @Assisted ResultSet resultSet, @Assisted File outputFile) {
        this.resultSet = resultSet;
        this.outputFile = outputFile;
        this.csvWriterFactory = csvWriterFactory;
    }

    public void write() throws SQLException, IOException {
        try (FileWriter fileWriter = new FileWriter(outputFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter printWriter = new PrintWriter(bufferedWriter);
             CSVWriter csvWriter = csvWriterFactory.create(printWriter)
        ) {
            csvWriter.writeAll(resultSet, true);
        }
    }
}
