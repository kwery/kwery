package com.kwery.services.scheduler;

import java.io.File;

public interface CsvToHtmlConverterFactory {
    CsvToHtmlConverter create(File csvFile);
}
