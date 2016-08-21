package controllers.util;

import com.google.common.collect.ImmutableMap;
import models.Datasource;
import models.User;
import org.openqa.selenium.Cookie;

import java.util.Map;

import static models.Datasource.Type.MYSQL;

public class TestUtil {
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
        datasource.setUrl("url");
        datasource.setUsername("username");
        datasource.setPassword("password");
        datasource.setLabel("label");
        datasource.setType(MYSQL);
        return datasource;
    }

    public static Cookie sessionCookie(String value) {
        return new Cookie("NINJA_SESSION", value);
    }

    public static Cookie usernameCookie() {
        return sessionCookie(USER_NAME_COOKIE);
    }

}
