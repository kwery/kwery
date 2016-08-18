package controllers.mock;

import controllers.UserController;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static controllers.util.TestUtil.user;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerMockTest extends AbstractControllerMockTest {
    @Mock
    private UserDao userDao;
    private UserController userController;

    @Before
    public void before() {
        userController = new UserController();
        userController.setMessages(messages);
        userController.setUserDao(userDao);
    }

    @Test
    public void testSuccess() {
        User user = user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(null);


        doNothing().when(userDao).save(user);

        String successMessage = "success";
        mockMessages(ADMIN_USER_ADDITION_SUCCESS, successMessage, user.getUsername());

        ActionResult actionResult = actionResult(userController.addAdminUser(context, user));

        assertEquals("ActionResult message matches", successMessage, actionResult.getMessage());
        assertEquals("ActionResult status matches", ActionResult.Status.success, actionResult.getStatus());
    }

    @Test
    public void testFailure() {
        User user = user();

        when(userDao.getByUsername(user.getUsername())).thenReturn(user);

        String message = "failure";
        mockMessages(ADMIN_USER_ADDITION_FAILURE, message, user.getUsername());

        ActionResult actionResult = actionResult(userController.addAdminUser(context, user));

        assertEquals("ActionResult message matches", message, actionResult.getMessage());
        assertEquals("ActionResult status matches", ActionResult.Status.failure, actionResult.getStatus());
    }
}
