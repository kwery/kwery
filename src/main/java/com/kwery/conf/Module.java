package com.kwery.conf;

import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.job.JobExecutor;
import com.kwery.services.job.JobFactory;
import com.kwery.services.job.JobSchedulerTaskFactory;
import com.kwery.services.job.parameterised.SqlQueryNormalizerFactory;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractorFactory;
import com.kwery.services.kwerydirectory.KweryDirectoryChecker;
import com.kwery.services.license.LicenseChecker;
import com.kwery.services.mail.converter.CsvToReportEmailSectionConverterFactory;
import com.kwery.services.scheduler.PreparedStatementExecutorFactory;
import com.kwery.services.scheduler.ResultSetProcessorFactory;
import com.kwery.services.search.SearchIndexer;
import com.kwery.utils.CsvReaderFactory;
import com.kwery.utils.CsvReaderFactoryImpl;
import com.kwery.utils.CsvWriterFactory;
import com.kwery.utils.CsvWriterFactoryImpl;
import it.sauronsoftware.cron4j.Scheduler;
import ninja.utils.NinjaConstant;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Singleton
public class Module extends AbstractModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected void configure() {
        install(new FactoryModuleBuilder().build(ResultSetProcessorFactory.class));
        install(new FactoryModuleBuilder().build(PreparedStatementExecutorFactory.class));
        install(new FactoryModuleBuilder().build(CsvToReportEmailSectionConverterFactory.class));
        install(new FactoryModuleBuilder().build(SqlQueryNormalizerFactory.class));
        install(new FactoryModuleBuilder().build(SqlQueryParameterExtractorFactory.class));

        install(new FactoryModuleBuilder().build(JobSchedulerTaskFactory.class));
        install(new FactoryModuleBuilder().build(JobFactory.class));

        bind(SearchIndexer.class);
        bind((KweryDirectoryChecker.class));
        bind(CsvWriterFactory.class).to(CsvWriterFactoryImpl.class);
        bind(CsvReaderFactory.class).to(CsvReaderFactoryImpl.class);
        bind(LicenseChecker.class);
        //Binding is done so that Ninja can make use of the life cycle methods in this class
        bind(JobExecutor.class);
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

    File createDirectory(String name) throws URISyntaxException {
        File dir = null;
        //TODO - Is there a better way to do this?
        if (NinjaConstant.MODE_TEST.equals(System.getProperty(NinjaConstant.MODE_KEY_NAME))) {
            dir = Files.createTempDir();
            File finalBase = dir;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileUtils.deleteDirectory(finalBase);
                } catch (IOException e) {
                    logger.error("Failed to delete {} on shutdown", name);
                }
            }));
        } else {
            //This is there only to help in development, should not be used in production as we want to mandate the files being is the same folder as the application jar
            String kweryBaseDirectory = System.getProperty("kweryBaseDirectory");
            if (!"".equals(kweryBaseDirectory)) {
                logger.info("Kwery base directory has been passed through JVM arguments, using that - " + kweryBaseDirectory);
                dir = new File(kweryBaseDirectory, name);
            } else {
                //Idea is to create the base directory for storing files in the same directory as the one in which Kwery application runs
                //http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
                //Get the directory from which the jar file running Kwery was started
                Path path = Paths.get(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                dir = new File(path.toFile(), name);
            }
        }

        if (dir.exists()) {
            logger.info("Directory {} exists", dir);
        } else {
            logger.info("Creating directory {}", dir);

            if (!dir.mkdir()) {
                logger.error("Could not create directory {}", dir);
                logger.error("Kwery shutting down");
                System.exit(-1);
            }
        }

        return dir;
    }

    @Provides @Singleton
    protected TemplateDirectory templateDirectory() throws URISyntaxException {
        return new TemplateDirectory(createDirectory("kwery_templates"));
    }

    @Provides @Singleton
    protected KweryDirectory kweryDirectory() throws URISyntaxException {
        return new KweryDirectory(createDirectory("kwery_files"));
    }

    @Provides @Singleton
    protected ITemplateEngine htmlTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        return new StringTemplateResolver();
    }
}
