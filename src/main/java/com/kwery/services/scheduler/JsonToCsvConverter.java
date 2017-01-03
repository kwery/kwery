package com.kwery.services.scheduler;

import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Singleton
public class JsonToCsvConverter {
    public String convert(String json) throws IOException {
        StringWriter stringWriter = new StringWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<List<?>>> typeReference = new TypeReference<List<List<?>>>() {};
        List<List<String>> table = objectMapper.readValue(json, typeReference);

        CSVWriter csvWriter = new CSVWriter(stringWriter, ',');
        for (List<String> rows : table) {
            csvWriter.writeNext(rows.toArray(new String[rows.size()]));
        }

        return stringWriter.getBuffer().toString();
    }

    public static void main(String[] args) throws IOException {
        String json = "[[\"name\", \"age\"],[\"purvi\", \"2\"]]";
        System.out.println(new JsonToCsvConverter().convert(json));
    }
}
