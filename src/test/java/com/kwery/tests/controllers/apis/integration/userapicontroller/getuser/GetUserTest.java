package com.kwery.tests.controllers.apis.integration.userapicontroller.getuser;

import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import com.kwery.models.User;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.conf.Routes.USER;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GetUserTest extends AbstractPostLoginApiTest {
    @Test
    public void test() throws IOException {
        User fromApi = user(ninjaTestBrowser.makeJsonRequest(getUrl(USER)));
        assertThat(fromApi.getId(), is(loggedInUser.getId()));
        assertThat(fromApi.getUsername(), is(loggedInUser.getUsername()));
        assertThat(fromApi.getPassword(), is(loggedInUser.getPassword()));
    }
}
