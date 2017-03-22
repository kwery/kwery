package com.kwery.conf;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobTaskFactory;
import com.kwery.services.job.SchedulerListenerImpl;
import com.kwery.services.job.TaskExecutorListenerImpl;
import com.kwery.services.kwerydirectory.KweryDirectoryChecker;
import com.kwery.services.license.LicenseChecker;
import com.kwery.services.scheduler.CsvToHtmlConverterFactory;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.services.search.SearchIndexer;
import com.kwery.utils.CsvReaderFactory;
import com.kwery.utils.CsvReaderFactoryImpl;
import com.kwery.utils.CsvWriterFactory;
import com.kwery.utils.CsvWriterFactoryImpl;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutorListener;
import ninja.utils.NinjaConstant;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class Module extends AbstractModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void configure() {
        bind(SchedulerListener.class).to(SchedulerListenerImpl.class);
        bind(TaskExecutorListener.class).to(TaskExecutorListenerImpl.class);
        install(new FactoryModuleBuilder().build(ResultSetProcessorFactory.class));
        install(new FactoryModuleBuilder().build(PreparedStatementExecutorFactory.class));
        install(new FactoryModuleBuilder().build(JobTaskFactory.class));
        install(new FactoryModuleBuilder().build(com.kwery.services.job.SqlQueryTaskFactory.class));
        install(new FactoryModuleBuilder().build(CsvToHtmlConverterFactory.class));
        bind(SearchIndexer.class);
        bind((KweryDirectoryChecker.class));
        bind(CsvWriterFactory.class).to(CsvWriterFactoryImpl.class);
        bind(CsvReaderFactory.class).to(CsvReaderFactoryImpl.class);
        bind(LicenseChecker.class);
    }

    @Provides
    protected Scheduler provideScheduler() {
        return new Scheduler();
    }

    @Provides
    protected SqlQueryExecutionModel provideQueryRunExecution() {
        return new SqlQueryExecutionModel();
    }

    @Provides
    protected SqlQueryModel provideQueryRun() {
        return new SqlQueryModel();
    }

    @Provides @Singleton
    protected KweryDirectory kweryDirectory() throws URISyntaxException {
        File base = null;
        //TODO - Is there a better way to do this?
        if (NinjaConstant.MODE_TEST.equals(System.getProperty(NinjaConstant.MODE_KEY_NAME))) {
            base = Files.createTempDir();
            File finalBase = base;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileUtils.deleteDirectory(finalBase);
                } catch (IOException e) {
                    logger.error("Failed to delete KweryDirectory on shutdown");
                }
            }));
        } else {
            //This is there only to help in development, should not be used in production as we want to mandate the files being is the same folder as the application jar
            String kweryBaseDirectory = System.getProperty("kweryBaseDirectory");
            if (!"".equals(kweryBaseDirectory)) {
                logger.info("Kwery base directory has been passed through JVM arguments, using that - " + kweryBaseDirectory);
                base = new File(kweryBaseDirectory, "kwery_files");
            } else {
                //Idea is to create the base directory for storing files in the same directory as the one in which Kwery application runs
                //http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
                //Get the directory from which the jar file running Kwery was started
                Path path = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                base = new File(path.toFile(), "kwery-files");
            }
        }

        if (base.exists()) {
            logger.info("Base directory {} exists", base);
        } else {
            logger.info("Creating base directory {} to stores result files", base);

            if (!base.mkdir()) {
                logger.error("Could not create base directory {} to store files", base);
                logger.error("Kwery shutting down");
                System.exit(-1);
            }
        }

        return new KweryDirectory(base);
    }
}
