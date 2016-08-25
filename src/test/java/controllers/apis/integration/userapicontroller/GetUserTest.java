package controllers.apis.integration.userapicontroller;

import models.User;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GetUserTest extends PostLoginApiTest {
    @Test
    public void test() throws IOException {
        User fromApi = user(ninjaTestBrowser.makeJsonRequest(getUserApi));
        assertThat(fromApi.getId(), is(user.getId()));
        assertThat(fromApi.getUsername(), is(user.getUsername()));
        assertThat(fromApi.getPassword(), is(user.getPassword()));
    }
}
