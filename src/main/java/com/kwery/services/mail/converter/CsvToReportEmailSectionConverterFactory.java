package com.kwery.services.mail.converter;

import java.io.File;

public interface CsvToReportEmailSectionConverterFactory {
    CsvToReportEmailSectionConverter create(File csvFile);
}
