package controllers.mock;

import com.google.common.base.Optional;
import controllers.MessageKeys;
import controllers.UserController;
import dao.UserDao;
import models.User;
import ninja.Result;
import ninja.i18n.Messages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerMockTest {
    @Mock
    private UserDao userDao;
    @Mock
    private Messages messages;
    private UserController userController;

    private String username = "purvi";

    @Before
    public void before() {
        userController = new UserController();
        userController.setMessages(messages);
        userController.setUserDao(userDao);
    }

    @Test
    public void testSuccess() {
        when(userDao.getByUsername(username)).thenReturn(null);

        User user = new User();
        user.setUsername(username);
        user.setPassword("");

        doNothing().when(userDao).save(user);

        String successMessage = "success";
        when(messages.get(MessageKeys.ADMIN_USER_CREATION_SUCCESS, Optional.absent(), username)).thenReturn(Optional.of(successMessage));


        Result creationResult = userController.createAdminUser(user);

        ActionResult actionResult = (ActionResult) creationResult.getRenderable();

        assertEquals("ActionResult message matches", successMessage, actionResult.getMessage());
        assertEquals("ActionResult status matches", ActionResult.Status.success, actionResult.getStatus());
    }

    @Test
    public void testFailure() {
        User user = new User();
        user.setUsername(username);
        user.setPassword("");

        when(userDao.getByUsername(username)).thenReturn(user);

        String message = "failure";
        when(messages.get(MessageKeys.ADMIN_USER_CREATION_FAILURE, Optional.absent(), username)).thenReturn(Optional.of(message));

        Result creationResult = userController.createAdminUser(user);

        ActionResult actionResult = (ActionResult) creationResult.getRenderable();

        assertEquals("ActionResult message matches", message, actionResult.getMessage());
        assertEquals("ActionResult status matches", ActionResult.Status.failure, actionResult.getStatus());
    }
}
