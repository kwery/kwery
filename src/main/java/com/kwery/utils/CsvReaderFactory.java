package com.kwery.utils;

import au.com.bytecode.opencsv.CSVReader;

import java.io.Reader;

public interface CsvReaderFactory {
    CSVReader create(Reader reader);
}
