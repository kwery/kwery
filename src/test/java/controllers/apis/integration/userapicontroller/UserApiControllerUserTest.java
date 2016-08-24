package controllers.apis.integration.userapicontroller;

import conf.Routes;
import controllers.apis.integration.PostLoginApiTest;
import controllers.util.TestUtil;
import models.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserApiControllerUserTest extends PostLoginApiTest {
    @Before
    public void before() {
        super.before();
    }
    @Test
    public void test() throws IOException {
        User fromApi = user(ninjaTestBrowser.makeJsonRequest(getUrl(Routes.USER)));
        assertThat(fromApi.getId(), is(savedUser.getId()));
        assertThat(fromApi.getUsername(), is(savedUser.getUsername()));
        assertThat(fromApi.getPassword(), is(savedUser.getPassword()));
    }
}
