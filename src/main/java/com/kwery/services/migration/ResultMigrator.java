package com.kwery.services.migration;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.kwery.dao.KweryVersionDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.models.KweryVersionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.services.kweryversion.KweryVersionUpdater;
import com.kwery.services.scheduler.JsonToCsvConverter;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import com.kwery.utils.KweryDirectory;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.kwery.utils.ServiceStartUpOrderConstant.RESULT_MIGRATION_ORDER;

@Singleton
public class ResultMigrator {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected final JsonToCsvConverter jsonToCsvConverter;
    protected final KweryDirectory kweryDirectory;
    protected final SqlQueryExecutionDao sqlQueryExecutionDao;
    protected final KweryVersionDao kweryVersionDao;
    protected final NinjaProperties ninjaProperties;

    @Inject
    public ResultMigrator(JsonToCsvConverter jsonToCsvConverter, KweryDirectory kweryDirectory,
                          SqlQueryExecutionDao sqlQueryExecutionDao, KweryVersionDao kweryVersionDao, NinjaProperties ninjaProperties) {
        this.jsonToCsvConverter = jsonToCsvConverter;
        this.kweryDirectory = kweryDirectory;
        this.sqlQueryExecutionDao = sqlQueryExecutionDao;
        this.kweryVersionDao = kweryVersionDao;
        this.ninjaProperties = ninjaProperties;
    }

    @Start(order = RESULT_MIGRATION_ORDER)
    public void migrate() {
        if (ninjaProperties.isTest()) {
            logger.info("Mode is test, hence skipping result migration from Derby to FS");
            return;
        }

        KweryVersionModel kweryVersionModel = kweryVersionDao.get();

        if (kweryVersionModel.getVersion().equals(KweryVersionUpdater.RELEASE_SNAPSHOT_1_5_1)) {
            logger.info("Migration of results from Derby to file system - start");

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
                            logger.info("Updating model");
                            model.setResult(null);
                            model.setResultFileName(resultFile.getName());
                            logger.info("Updated model successfully");
                            sqlQueryExecutionDao.save(model);
                        } catch (IOException e) {
                            logger.error("Exception while converting json to csv for SQL query execution id {}", model.getId(), e);
                            logger.error("Shutting down Kwery");
                            System.exit(-1);
                        }
                    } else {
                        logger.info("Result is empty for sql query execution {} with sql query id {} job id {} that started on {} and ended on {}",
                                model.getId(), model.getSqlQuery().getId(), model.getJobExecutionModel().getJobModel().getId(),
                                new Date(model.getJobExecutionModel().getExecutionStart()), new Date(model.getJobExecutionModel().getExecutionEnd()));
                    }

                    logger.info("Migration of result for sql query execution {} with sql query id {} job id {} that started on {} and ended on {} - end",
                            model.getId(), model.getSqlQuery().getId(), model.getJobExecutionModel().getJobModel().getId(),
                            new Date(model.getJobExecutionModel().getExecutionStart()), new Date(model.getJobExecutionModel().getExecutionEnd()));
                }
                filter.setPageNumber(filter.getPageNumber() + 1);
            } while (!models.isEmpty());
            logger.info("Migration of results from Derby to file system - end");
        } else {
            logger.info("Skipping result migration since Kwery is running at version - " + KweryVersionUpdater.RELEASE_SNAPSHOT_1_5_1);
        }
    }
}
