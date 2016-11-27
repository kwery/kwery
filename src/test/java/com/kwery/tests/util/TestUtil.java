package com.kwery.tests.util;

import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.models.SqlQueryExecution.Status;
import com.kwery.models.User;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;

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

    public static SqlQuery listMySqlUserQuery(Datasource datasource) {
        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setDatasource(datasource);
        sqlQuery.setCronExpression("* * * * *");
        sqlQuery.setLabel("test");
        sqlQuery.setQuery("select * from mysql.user");
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
}
