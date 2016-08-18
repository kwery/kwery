package controllers.util;

import models.Datasource;
import models.User;

import static models.Datasource.Type.MYSQL;

public class TestUtil {
    public static User user() {
        User user = new User();
        user.setUsername("purvi");
        user.setPassword("password");
        return user;
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
}
