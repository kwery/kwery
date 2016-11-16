package models;

import org.junit.Test;

import static models.Datasource.Type.MYSQL;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class DatasourceToStringPasswordObfuscationTest {
    @Test
    public void test() {
        Datasource datasource = new Datasource();
        datasource.setId(1);
        datasource.setUrl("foo.com");
        datasource.setUsername("username");
        datasource.setPassword("secret");
        datasource.setType(MYSQL);
        datasource.setPort(3306);

        assertThat(datasource.toString(), not(containsString(datasource.getPassword())));
    }
}
