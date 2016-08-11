package controllers;

import conf.Routes;
import ninja.Result;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationControllerMockTest {
    private ApplicationController applicationController;

    @Before
    public void setup() {
        applicationController = new ApplicationController();
    }

    @Test
    public void testWelcome() {
        Result result = applicationController.welcome();
        assertEquals("Result from createAdminUserGet method has path", Routes.CREATE_ADMIN_USER, String.valueOf(((Map)result.getRenderable()).get("path")));
    }
}
