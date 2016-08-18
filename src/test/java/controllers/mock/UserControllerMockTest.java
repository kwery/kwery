package controllers.mock;

import controllers.UserController;
import dao.UserDao;
import models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static controllers.util.TestUtil.user;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerMockTest extends ControllerMockTest {
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
        mockMessages(ADMIN_USER_ADDITION_SUCCESS, user.getUsername());
        assertSuccess(actionResult(userController.addAdminUser(context, user)));
    }

    @Test
    public void testFailure() {
        User user = user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(user);
        mockMessages(ADMIN_USER_ADDITION_FAILURE, user.getUsername());
        assertFailure(actionResult(userController.addAdminUser(context, user)));
    }
}
