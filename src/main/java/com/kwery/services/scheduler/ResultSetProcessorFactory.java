package com.kwery.services.scheduler;

import java.io.File;
import java.sql.ResultSet;

public interface ResultSetProcessorFactory {
    ResultSetToCsvWriter create(ResultSet resultSet, File file);
}
