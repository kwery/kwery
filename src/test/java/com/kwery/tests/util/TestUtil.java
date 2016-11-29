package com.kwery.tests.util;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.EmailConfiguration;
import com.kwery.models.SmtpConfiguration;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.models.SqlQueryExecution.Status;
import com.kwery.models.User;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.EmailConfiguration.COLUMN_BCC;
import static com.kwery.models.EmailConfiguration.COLUMN_FROM_EMAIL;
import static com.kwery.models.EmailConfiguration.COLUMN_REPLY_TO;
import static com.kwery.models.EmailConfiguration.TABLE_EMAIL_CONFIGURATION;
import static com.kwery.models.SmtpConfiguration.COLUMN_HOST;
import static com.kwery.models.SmtpConfiguration.COLUMN_PASSWORD;
import static com.kwery.models.SmtpConfiguration.COLUMN_PORT;
import static com.kwery.models.SmtpConfiguration.COLUMN_SSL;
import static com.kwery.models.SmtpConfiguration.COLUMN_USERNAME;
import static com.kwery.models.SmtpConfiguration.TABLE_SMTP_CONFIGURATION;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;
import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class TestUtil {
    public static final int TIMEOUT_SECONDS = 30;

    //Corresponds to the starting id set in *sql file
    public static final int DB_START_ID = 100;

    public static User user() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        return user;
    }

    public static Datasource datasource() {
        Datasource datasource = new Datasource();
        datasource.setUrl("0.0.0.0");
        datasource.setPort(3306);
        datasource.setUsername("root");
        datasource.setPassword("root");
        datasource.setLabel("label");
        datasource.setType(MYSQL);
        return datasource;
    }

    public static SqlQuery queryRun() {
        SqlQuery q = new SqlQuery();
        q.setQuery("select * from foo");
        q.setLabel("test query run");
        q.setCronExpression("* * * * *");
        return q;
    }

    public static SqlQuery sleepSqlQuery(Datasource datasource) {
        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setCronExpression("* * * * *");
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select sleep(86440)");
        return sqlQuery;
    }

    public static SqlQueryDto queryRunDto() {
        SqlQueryDto dto = new SqlQueryDto();
        dto.setQuery("select * from foo");
        dto.setLabel("test");
        dto.setCronExpression("* * * * *");
        return dto;
    }

    public static SqlQueryExecution queryRunExecution() {
        return queryRunExecution(SUCCESS);
    }

    public static SqlQueryExecution queryRunExecution(Status status) {
        SqlQueryExecution e = new SqlQueryExecution();
        e.setExecutionStart(1l);
        e.setExecutionEnd(2l);
        e.setResult("result");
        e.setStatus(status);
        e.setExecutionId("ksjdfjld");
        return e;
    }

    public static SmtpConfiguration smtpConfigurationWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SmtpConfiguration config = podamFactory.manufacturePojo(SmtpConfiguration.class);
        config.setId(null);
        return config;
    }

    public static SmtpConfiguration smtpConfiguration() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        SmtpConfiguration config = podamFactory.manufacturePojo(SmtpConfiguration.class);
        config.setId(1);
        return config;
    }

    public static EmailConfiguration emailConfigurationWithoutId() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        EmailConfiguration emailConfiguration = podamFactory.manufacturePojo(EmailConfiguration.class);
        emailConfiguration.setId(null);
        return emailConfiguration;
    }

    public static EmailConfiguration emailConfiguration() {
        PodamFactory podamFactory = new PodamFactoryImpl();
        EmailConfiguration emailConfiguration = podamFactory.manufacturePojo(EmailConfiguration.class);
        emailConfiguration.setId(1);
        return emailConfiguration;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            System.out.println(smtpConfigurationWithoutId());
        }
    }

    public static EmailConfiguration emailConfigurationDbSetUp() {
        EmailConfiguration emailConfiguration = emailConfiguration();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(TABLE_EMAIL_CONFIGURATION)
                                .row()
                                .column(EmailConfiguration.COLUMN_ID, emailConfiguration.getId())
                                .column(COLUMN_FROM_EMAIL, emailConfiguration.getFrom())
                                .column(COLUMN_BCC, emailConfiguration.getBcc())
                                .column(COLUMN_REPLY_TO, emailConfiguration.getReplyTo())
                                .end()
                                .build()
                )
        ).launch();

        return emailConfiguration;
    }

    public static SmtpConfiguration smtpConfigurationDbSetUp() {
        SmtpConfiguration smtpConfiguration = smtpConfiguration();

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        insertInto(TABLE_SMTP_CONFIGURATION)
                                .row()
                                .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                .column(COLUMN_HOST, smtpConfiguration.getHost())
                                .column(COLUMN_PORT, smtpConfiguration.getPort())
                                .column(COLUMN_SSL, smtpConfiguration.isSsl())
                                .column(COLUMN_USERNAME, smtpConfiguration.getUsername())
                                .column(COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                .end()
                                .build()
                )
        ).launch();

        return smtpConfiguration;
    }
}
