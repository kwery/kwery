package controllers.mock;

import com.google.common.base.Optional;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Messages;
import org.mockito.Mock;
import views.ActionResult;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static views.ActionResult.Status.failure;
import static views.ActionResult.Status.success;

public class ControllerMockTest {
    @Mock
    protected Messages messages;
    @Mock
    protected Context context;

    protected String dummyString = "foo";

    protected void mockMessages(String messageKey) {
        when(messages.get(eq(messageKey), eq(context), any(Optional.class))).thenReturn(Optional.of(dummyString));
    }

    protected void mockMessages(String messageKey, String str0) {
       when(messages.get(eq(messageKey), eq(context), any(Optional.class), eq(str0))).thenReturn(Optional.of(dummyString));
    }

    protected void mockMessages(String messageKey, String str0, String str1) {
        when(messages.get(eq(messageKey), eq(context), any(Optional.class), eq(str0), eq(str1))).thenReturn(Optional.of(dummyString));
    }

    protected ActionResult actionResult(Result result) {
        return (ActionResult) result.getRenderable();
    }

    protected void assertSuccess(ActionResult actionResult) {
        assertThat(actionResult.getMessage(), is(dummyString));
        assertThat(actionResult.getStatus(), is(success));
    }

    protected void assertSuccessNextAction(ActionResult actionResult, String nextAction) {
        assertThat(actionResult.getMessage(), is(dummyString));
        assertThat(actionResult.getStatus(), is(success));
        assertThat(actionResult.getNextActionName(), is(dummyString));
        assertThat(actionResult.getNextAction(), is(nextAction));
    }

    protected void assertFailure(ActionResult actionResult) {
        assertThat(actionResult.getMessage(), is(dummyString));
        assertThat(actionResult.getStatus(), is(failure));
    }
}
