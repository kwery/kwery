package com.kwery.utils;

import au.com.bytecode.opencsv.CSVReader;

import java.io.Reader;

import static au.com.bytecode.opencsv.CSVParser.DEFAULT_ESCAPE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static au.com.bytecode.opencsv.CSVWriter.DEFAULT_SEPARATOR;

public class CsvReaderFactoryImpl implements CsvReaderFactory {
    @Override
    public CSVReader create(Reader reader) {
        return new CSVReader(reader, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER);
    }
}
