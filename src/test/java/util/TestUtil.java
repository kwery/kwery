package util;

import com.google.common.collect.ImmutableMap;
import dtos.SqlQueryDto;
import models.Datasource;
import models.SqlQuery;
import models.SqlQueryExecution;
import models.SqlQueryExecution.Status;
import models.User;
import org.openqa.selenium.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static models.Datasource.Type.MYSQL;
import static models.SqlQueryExecution.Status.SUCCESS;

public class TestUtil {
    protected static Logger logger = LoggerFactory.getLogger(TestUtil.class);

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
