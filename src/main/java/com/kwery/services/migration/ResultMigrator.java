package com.kwery.services.migration;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.scheduler.JsonToCsvConverter;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.utils.KweryDirectory;
import com.kwery.utils.KweryDirectoryFactory;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class ResultMigrator {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected JsonToCsvConverter jsonToCsvConverter;
    protected KweryDirectoryFactory kweryDirectoryFactory;
    protected SqlQueryExecutionDao sqlQueryExecutionDao;

    @Inject
    public ResultMigrator(JsonToCsvConverter jsonToCsvConverter, KweryDirectoryFactory kweryDirectoryFactory, SqlQueryExecutionDao sqlQueryExecutionDao) {
        this.jsonToCsvConverter = jsonToCsvConverter;
        this.kweryDirectoryFactory = kweryDirectoryFactory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
    }

    @Start(order = 20)
    public void migrate() {
        logger.info("Migration of results from Derby to file system - start");
        try {
            //http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
            //Get the directory from which the jar file running Kwery was started
            Path path = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            File base = new File(path.toFile(), "kwery-files");

            logger.info("Creating base directory {} to stores result files", base);

            if (!base.mkdir()) {
                logger.error("Could not create base directory {} to store files", base);
                logger.error("Kwery shutting down");
                System.exit(-1);
            }

            KweryDirectory kweryDirectory = kweryDirectoryFactory.create(base);
            kweryDirectory.createDirectories();

            SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
            filter.setStatuses(ImmutableList.of(SqlQueryExecutionModel.Status.SUCCESS));
            filter.setPageNumber(0);
            filter.setResultCount(50);

            List<SqlQueryExecutionModel> models = new ArrayList<>();
            do {
                models = sqlQueryExecutionDao.filter(filter);

                for (SqlQueryExecutionModel model : models) {
                    logger.info("Migration of result for sql query execution {} with sql query id {} job id {} that started on {} and ended on {} - start",
                            model.getId(), model.getSqlQuery().getId(), model.getJobExecutionModel().getJobModel().getId(),
                            new Date(model.getJobExecutionModel().getExecutionStart()), new Date(model.getJobExecutionModel().getExecutionEnd()));

                    String jsonResult = model.getResult();

                    if (!"".equals(Strings.nullToEmpty(jsonResult))) {
                        try {
                            String csv = jsonToCsvConverter.convert(jsonResult);
                            File resultFile = kweryDirectory.createFile();
                            logger.info("File {} created to move result from derby", resultFile);
                            try {
                                Files.write(Paths.get(resultFile.toURI()), csv.getBytes(), StandardOpenOption.APPEND);
                                logger.info("Result wrote to file successfully");
                            } catch (IOException e) {
                                logger.error("Exception while writing CSV to file {}", resultFile, e);
                                logger.error("Shutting down Kwery");
                                System.exit(-1);
                            }
                            logger.info("Updating result to null");
                            model.setResult(null);
                            logger.info("Updated result to null successfully");
                            sqlQueryExecutionDao.save(model);
                        } catch (IOException e) {
                            logger.error("Exception while converting json to csv for SQL query execution id {}", model.getId(), e);
                            logger.error("Shutting down Kwery");
                            System.exit(-1);
                        }
                    } else {
                        logger.info("Result is empty for sql query execution {} with sql query id {} job id {} that started on {} and ended on {} - end",
                                model.getId(), model.getSqlQuery().getId(), model.getJobExecutionModel().getJobModel().getId(),
                                new Date(model.getJobExecutionModel().getExecutionStart()), new Date(model.getJobExecutionModel().getExecutionEnd()));
                    }

                    logger.info("Migration of result for sql query execution {} with sql query id {} job id {} that started on {} and ended on {} - end",
                            model.getId(), model.getSqlQuery().getId(), model.getJobExecutionModel().getJobModel().getId(),
                            new Date(model.getJobExecutionModel().getExecutionStart()), new Date(model.getJobExecutionModel().getExecutionEnd()));
                }
                filter.setPageNumber(filter.getPageNumber() + 1);
            } while (!models.isEmpty());
        } catch (URISyntaxException e) {
            logger.error("Exception while determining the base directory to store query results", e);
            logger.error("Shutting down Kwery");
            System.exit(-1);
        }
        logger.info("Migration of results from Derby to file system - end");
    }
}
