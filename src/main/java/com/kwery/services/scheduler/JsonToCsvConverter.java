package com.kwery.services.scheduler;

import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.utils.CsvWriterFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.List;

@Singleton
public class JsonToCsvConverter {
    private CsvWriterFactory csvWriterFactory;

    @Inject
    public JsonToCsvConverter(CsvWriterFactory csvWriterFactory) {
        this.csvWriterFactory = csvWriterFactory;
    }

    public String convert(String json) throws IOException {
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<List<?>>> typeReference = new TypeReference<List<List<?>>>() {};
        List<List<String>> table = objectMapper.readValue(json, typeReference);

        try (CSVWriter csvWriter = csvWriterFactory.create(stringWriter)) {
            for (List<String> rows : table) {
                csvWriter.writeNext(rows.toArray(new String[rows.size()]));
            }
        }

        return stringWriter.getBuffer().toString();
    }

    public static void main(String[] args) throws Exception {
/*
        List<List<String>> table = ImmutableList.of(
            ImmutableList.of("abhira,ra\"ma", "pu,rvi", "pavi,tra")
        );

        StringWriter stringWriter = new StringWriter();
        //try (CSVWriter csvWriter = new CSVWriter(stringWriter, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER)) {
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            for (List<String> rows : table) {
                csvWriter.writeNext(rows.toArray(new String[rows.size()]));
            }
        }

        String x = stringWriter.getBuffer().toString();
        System.out.println(x);

        StringReader stringReader = new StringReader(x);
        CSVReader csvReader = new CSVReader(stringReader);
        for (String[] strings : csvReader.readAll()) {
            for (String string : strings) {
                System.out.println(string);
            }
        }

*/

        System.out.println(Paths.get(JsonToCsvConverter.class.getProtectionDomain().getCodeSource().getLocation().toURI()));
    }
}
