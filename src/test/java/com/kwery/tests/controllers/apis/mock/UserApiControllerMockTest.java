package com.kwery.tests.controllers.apis.mock;

import com.kwery.models.User;
import ninja.Result;
import ninja.session.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.kwery.tests.util.TestSession;
import com.kwery.tests.util.TestUtil;

import static com.kwery.controllers.MessageKeys.ADMIN_USER_ADDITION_FAILURE;
import static com.kwery.controllers.MessageKeys.ADMIN_USER_ADDITION_NEXT_ACTION;
import static com.kwery.controllers.MessageKeys.ADMIN_USER_ADDITION_SUCCESS;
import static com.kwery.controllers.MessageKeys.LOGIN_SUCCESS;
import static com.kwery.controllers.apis.UserApiController.SESSION_USERNAME_KEY;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserApiControllerMockTest extends AbstractUserApiControllerMockTest {
    @Mock
    private Session session;

    @Test
    public void testAddUserSuccess() {
        User user = TestUtil.user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(null);
        doNothing().when(userDao).save(user);

        when(context.getSession()).thenReturn(session);

        doNothing().when(session).put(SESSION_USERNAME_KEY, user.getUsername());

        mockMessages(ADMIN_USER_ADDITION_SUCCESS, user.getUsername());
        mockMessages(ADMIN_USER_ADDITION_NEXT_ACTION);
        assertSuccess(actionResult(userApiController.addAdminUser(context, user, validation)));
    }

    @Test
    public void testAddUserFailure() {
        User user = TestUtil.user();
        when(userDao.getByUsername(user.getUsername())).thenReturn(user);
        mockMessages(ADMIN_USER_ADDITION_FAILURE, user.getUsername());
        assertFailure(actionResult(userApiController.addAdminUser(context, user, validation)));
    }

    @Test
    public void testLoginSuccess() {
        User user = TestUtil.user();
        when(userDao.getUser(user.getUsername(), user.getPassword())).thenReturn(user);

        session = new TestSession();

        when(context.getSession()).thenReturn(session);

        mockMessages(LOGIN_SUCCESS, user.getUsername());
        assertSuccess(actionResult(userApiController.login(context, user)));

        assertThat(session.get(SESSION_USERNAME_KEY), is(user.getUsername()));
    }

    @Test
    public void testUser() {
        User user = TestUtil.user();
        user.setId(1);

        when(userDao.getByUsername(user.getUsername())).thenReturn(user);

        session = new TestSession();
        session.put(SESSION_USERNAME_KEY, user.getUsername());

        when(context.getSession()).thenReturn(session);

        Result result = userApiController.user(context);

        assertThat(result.getRenderable(), instanceOf(User.class));

        User fromResult = (User) result.getRenderable();

        assertThat(fromResult.getUsername(), is(user.getUsername()));
        assertThat(fromResult.getPassword(), is(user.getPassword()));
        assertThat(fromResult.getId(), is(1));
    }
}
