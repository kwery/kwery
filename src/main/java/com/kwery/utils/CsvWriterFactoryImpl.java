package com.kwery.utils;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.Writer;

import static au.com.bytecode.opencsv.CSVParser.DEFAULT_ESCAPE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

public class CsvWriterFactoryImpl implements CsvWriterFactory {
    @Override
    public CSVWriter create(Writer writer) {
        return new CSVWriter(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, System.lineSeparator());
    }
}
