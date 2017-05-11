package com.kwery.controllers.apis;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.services.job.parameterised.ParameterCsvExtractor;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractor;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractorFactory;
import com.kwery.services.mail.EmailValidator;
import com.kwery.utils.CsvReaderFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwery.controllers.apis.JobParameterValidator.Error.*;

public class JobParameterValidator {
    protected final CsvReaderFactory csvReaderFactory;
    protected final SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory;
    protected final EmailValidator emailValidator;
    protected final ParameterCsvExtractor parameterCsvExtractor;

    @Inject
    public JobParameterValidator(CsvReaderFactory csvReaderFactory,
                                 SqlQueryParameterExtractorFactory sqlQueryParameterExtractorFactory,
                                 EmailValidator emailValidator,
                                 ParameterCsvExtractor parameterCsvExtractor
                                 ) {
        this.csvReaderFactory = csvReaderFactory;
        this.sqlQueryParameterExtractorFactory = sqlQueryParameterExtractorFactory;
        this.emailValidator = emailValidator;
        this.parameterCsvExtractor = parameterCsvExtractor;
    }

    public List<Error> validate(JobDto jobDto) throws IOException {
        List<List<String>> csv = toCsv(jobDto.getParameterCsv());

        if (!csv.isEmpty()) {
            if (csv.size() < 2) {
                return ImmutableList.of(valuesNotPresent);
            }

            for (String header : csv.get(0)) {
                if ("".equals(header)) {
                    return ImmutableList.of(quoteLineEnding);
                }
            }

            int firstLineColumns = csv.get(0).size();
            for (List<String> strings : csv) {
                if (strings.size() != firstLineColumns) {
                    return ImmutableList.of(valueNameCountMismatch);
                }
            }

            List<String> parameters = new LinkedList<>();
            for (SqlQueryDto sqlQueryDto : jobDto.getSqlQueries()) {
                SqlQueryParameterExtractor extractor = sqlQueryParameterExtractorFactory.create(sqlQueryDto.getQuery());
                parameters.addAll(extractor.extract());
            }

            List<String> parametersCopy = new ArrayList<>(parameters);
            parametersCopy.removeAll(csv.get(0));
            if (!parametersCopy.isEmpty()) {
                return ImmutableList.of(parametersNotPresent);
            }


            List<String> headersCopy = new ArrayList<>(csv.get(0));
            headersCopy.remove(ParameterCsvExtractor.JOB_PARAMETER_CSV_EMAIL_HEADER);

            headersCopy.removeAll(parameters);
            if (!headersCopy.isEmpty()) {
                return ImmutableList.of(extraParametersPresent);
            }
        }

        return ImmutableList.of();
    }

    public List<String> getInvalidEmails(JobDto jobDto) throws IOException {
        List<Map<String, ?>> parameters = parameterCsvExtractor.extract(jobDto.getParameterCsv());
        List<String> emails = new LinkedList<>();
        for (Map<String, ?> parameter : parameters) {
            emails.addAll(parameterCsvExtractor.emails(parameter));
        }

        return emailValidator.filterInvalidEmails(emails);
    }

    public List<List<String>> toCsv(String parameterCsv) throws IOException {
        CSVReader csvReader = csvReaderFactory.create(new StringReader(parameterCsv));
        List<String[]> csv = csvReader.readAll();
        List<List<String>> ret = new ArrayList<>(csv.size());

        for (String[] csvRow : csv) {
            List<String> row = new ArrayList<>(csvRow.length);
            for (String col : csvRow) {
                row.add(col.trim());
            }
            ret.add(row);
        }

        return ret;
    }

    public enum Error {
        valuesNotPresent, valueNameCountMismatch, quoteLineEnding, parametersNotPresent, extraParametersPresent
    }
}
