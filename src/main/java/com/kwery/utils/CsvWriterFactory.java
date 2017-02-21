package com.kwery.utils;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.Writer;

public interface CsvWriterFactory {
    CSVWriter create(Writer writer);
}
