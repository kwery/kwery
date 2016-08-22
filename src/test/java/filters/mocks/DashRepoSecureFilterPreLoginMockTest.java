package filters.mocks;

import com.google.common.base.Optional;
import controllers.util.TestSession;
import filters.DashRepoSecureFilter;
import ninja.Context;
import ninja.FilterChain;
import ninja.Result;
import ninja.i18n.Messages;
import ninja.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static controllers.MessageKeys.USER_NOT_LOGGED_IN;
import static filters.DashRepoSecureFilter.LOGIN_JS_VIEW;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static views.ActionResult.Status.failure;

@RunWith(MockitoJUnitRunner.class)
public class DashRepoSecureFilterPreLoginMockTest {
    @Mock
    private Context context;
    @Mock
    private Messages messages;
    private Session session;
    private DashRepoSecureFilter filter;
    String message = "message";

    @Before
    public void before() {
        session = new TestSession();
        filter = new DashRepoSecureFilter();
        filter.setMessages(messages);
        when(context.getSession()).thenReturn(session);
        when(messages.get(eq(USER_NOT_LOGGED_IN), eq(context), any(Optional.class))).thenReturn(Optional.of(message));
    }

    @Test
    public void testApiRequest() {
        when(context.getRequestPath()).thenReturn("/api/foo");
        Result result = filter.filter(mock(FilterChain.class), context);
        ActionResult actionResult = (ActionResult) result.getRenderable();
        assertThat(actionResult.getStatus(), is(failure));
        assertThat(actionResult.getMessage(), is(message));
    }

    @Test
    public void testModuleRequest() {
        when(context.getRequestPath()).thenReturn("/module/foo");
        Result result = filter.filter(mock(FilterChain.class), context);
        assertThat(result.getTemplate(), is(LOGIN_JS_VIEW));
        assertThat(result.getContentType(), is("text/javascript"));
    }


    @Test(expected = AssertionError.class)
    public void testUnknownRequest() {
        when(context.getRequestPath()).thenReturn("/foo");
        filter.filter(mock(FilterChain.class), context);
    }
}
