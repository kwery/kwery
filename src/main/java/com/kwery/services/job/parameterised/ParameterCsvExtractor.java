package com.kwery.services.job.parameterised;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import org.apache.commons.validator.GenericValidator;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class ParameterCsvExtractor {
    public static final String JOB_PARAMETER_CSV_EMAIL_HEADER = "email_csv";

    public List<Map<String, ?>> extract(String parameterCsv) throws IOException {
        CSVReader csvReader = new CSVReader(new StringReader(parameterCsv));

        List<String[]> lines = csvReader.readAll();

        List<Map<String, ?>> parameters = new ArrayList<>(lines.size());

        for (int i = 1; i < lines.size(); ++i) {
            String[] values = lines.get(i);
            Map<String, Object> map = new HashMap<>(values.length);
            for (int j = 0; j < values.length; ++j) {
                map.put(lines.get(0)[j].trim(), parse(values[j].trim()));
            }

            parameters.add(map);
        }

        return parameters;
    }

    public List<String> emails(Map<String, ?> parameters) {
        if (parameters.containsKey(JOB_PARAMETER_CSV_EMAIL_HEADER)) {
            return Splitter.on(',').trimResults().omitEmptyStrings().splitToList(String.valueOf(parameters.get(JOB_PARAMETER_CSV_EMAIL_HEADER)));
        }

        return new LinkedList<>();
    }

    @VisibleForTesting
    public Object parse(String s) {
        if (GenericValidator.isByte(s)) {
            return Byte.parseByte(s);
        } else if (GenericValidator.isShort(s)) {
            return Short.parseShort(s);
        } else if (GenericValidator.isInt(s)) {
            return Integer.parseInt(s);
        } else if (GenericValidator.isLong(s)) {
            return Long.parseLong(s);
        } else if (GenericValidator.isFloat(s)) {
            return Float.parseFloat(s);
        } else if (GenericValidator.isDouble(s)) {
            return Double.parseDouble(s);
        } else {
            //If it is not of any of the above types, we assume it is a string
            return s;
        }
    }
}
