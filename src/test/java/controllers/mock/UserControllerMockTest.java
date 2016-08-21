package controllers.mock;

import controllers.UserController;
import controllers.util.TestSession;
import dao.UserDao;
import models.User;
import ninja.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_NEXT_ACTION;
import static controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static controllers.MessageKeys.LOGIN_SUCCESS;
import static controllers.UserController.ONBOARDING_POST_ADMIN_USER_CREATION_ACTION;
import static controllers.UserController.SESSION_USERNAME_KEY;
import static controllers.util.TestUtil.user;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerMockTest extends ControllerMockTest {
    @Mock
    private UserDao userDao;
    @Mock
    private Session session;
    private UserController userController;

    @Before
    public void before() {
        userController = new UserController();
        userController.setMessages(messages);
        userController.setUserDao(userDao);
    }

    @Test
    public void testAddUserSuccess() {
        User user = user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(null);
        doNothing().when(userDao).save(user);

        when(context.getSession()).thenReturn(session);

        doNothing().when(session).put(SESSION_USERNAME_KEY, user.getUsername());

        mockMessages(ADMIN_USER_ADDITION_SUCCESS, user.getUsername());
        mockMessages(ADMIN_USER_ADDITION_NEXT_ACTION);
        assertSuccessNextAction(actionResult(userController.addAdminUser(context, user)), ONBOARDING_POST_ADMIN_USER_CREATION_ACTION);
    }

    @Test
    public void testAddUserFailure() {
        User user = user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(user);
        mockMessages(ADMIN_USER_ADDITION_FAILURE, user.getUsername());
        assertFailure(actionResult(userController.addAdminUser(context, user)));
    }

    @Test
    public void testLoginSuccess() {
        User user = user();
        when(userDao.getUser(user.getUsername(), user.getPassword())).thenReturn(user);

        session = new TestSession();

        when(context.getSession()).thenReturn(session);

        mockMessages(LOGIN_SUCCESS, user.getUsername());
        assertSuccess(actionResult(userController.login(context, user)));

        assertThat(session.get(SESSION_USERNAME_KEY), is(user.getUsername()));
    }
}
