package controllers;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import ninja.Context;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.validation.ConstraintViolation;
import ninja.validation.FieldViolation;
import ninja.validation.Validation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerUtilTest {
    @Mock
    protected Context context;
    @Mock
    protected Validation validation;
    @Mock
    protected Messages messages;

    @Test
    public void test() {
        List<FieldViolation> fieldViolations = ImmutableList.of(
                new FieldViolation("username", new ConstraintViolation(
                        "username.foo.validation", "username", ""
                )),
                new FieldViolation("username", new ConstraintViolation(
                        "username.bar.validation", "username", ""
                )),
                new FieldViolation("password", new ConstraintViolation(
                        "password.foo.validation", "password", ""
                )),
                new FieldViolation("password", new ConstraintViolation(
                        "password.bar.validation", "password", ""
                ))
        );

        when(validation.getBeanViolations()).thenReturn(fieldViolations);

        for (String messageKey : ImmutableList.of("username.foo.validation", "username.bar.validation", "password.foo.validation", "password.bar.validation")) {
            when(messages.get(eq(messageKey), eq(context), any(Optional.class))).thenReturn(Optional.of(messageKey));
        }

        Map<String, List<String>> fieldMessages = ImmutableMap.of(
                "username", ImmutableList.of("username.foo.validation", "username.bar.validation"),
                "password", ImmutableList.of("password.foo.validation", "password.bar.validation")
        );

        assertThat(ControllerUtil.fieldMessages(validation, context, messages, Results.html()), is(fieldMessages));
    }
}
