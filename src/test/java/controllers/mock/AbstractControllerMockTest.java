package controllers.mock;

import com.google.common.base.Optional;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import views.ActionResult;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

@RunWith(MockitoJUnitRunner.class)
public class AbstractControllerMockTest {
    @Mock
    protected Messages messages;
    @Mock
    protected Context context;

    protected String message = "foo";

    protected void mockMessages(String messageKey, String str0) {
       when(messages.get(eq(messageKey), eq(context), any(Optional.class), eq(str0))).thenReturn(Optional.of(message));
    }

    protected void mockMessages(String messageKey, String str0, String str1) {
        when(messages.get(eq(messageKey), eq(context), any(Optional.class), eq(str0), eq(str1))).thenReturn(Optional.of(message));
    }

    protected ActionResult actionResult(Result result) {
        return (ActionResult) result.getRenderable();
    }

    protected void assertSuccess(ActionResult actionResult) {
        assertThat(actionResult.getMessage(), is(message));
        assertThat(actionResult.getStatus(), is(success));
    }

    protected void assertFailure(ActionResult actionResult) {
        assertThat(actionResult.getMessage(), is(message));
        assertThat(actionResult.getStatus(), is(failure));
    }
}
