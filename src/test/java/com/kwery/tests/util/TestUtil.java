package com.kwery.tests.util;

import com.google.common.collect.ImmutableMap;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQuery;
import com.kwery.models.SqlQueryExecution;
import com.kwery.models.SqlQueryExecution.Status;
import com.kwery.models.User;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.models.SqlQueryExecution.Status.SUCCESS;

public class TestUtil {
    public static final int TIMEOUT_SECONDS = 30;
    protected static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    //Corresponds to the starting id set in *sql file
    public static final int DB_START_ID = 100;

    public static final String USER_NAME_COOKIE = "3067e0b13d45acae3719c25f6bccfac007bfa8cf-___TS=1471772214856&username=fo";
    public static final String COOKIE_STRING = String.format("NINJA_SESSION=%s", USER_NAME_COOKIE);

    public static User user() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        return user;
    }

    public static Map<String, String> userParams() {
        return ImmutableMap.of(
                "username", "foo",
                "password", "password"
        );
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

    public static Cookie sessionCookie(String value) {
        return new Cookie("NINJA_SESSION", value);
    }

    public static Cookie usernameCookie() {
        return sessionCookie(USER_NAME_COOKIE);
    }

    public static boolean waitForMysql(String host, int port) {
        long start = System.currentTimeMillis();
        do {
            try (Connection connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?logger=com.mysql.cj.core.log.Slf4JLogger", host, port), "root", "root")) {
                return true;
            } catch (SQLException e) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e1) {
                }
            }
        } while (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - start) < 2);

        return false;
    }
}
