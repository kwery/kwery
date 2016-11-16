package com.kwery.tests.filters.mocks;

import com.kwery.filters.DashRepoSecureFilter;
import ninja.Context;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Messages;
import ninja.session.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.kwery.tests.util.TestSession;

import static com.kwery.controllers.apis.UserApiController.SESSION_USERNAME_KEY;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DashRepoSecureFilterPostLoginMockTest {
    @Mock
    private Context context;
    @Mock
    private Messages messages;
    @Mock
    private FilterChain filterChain;
    private Session session;
    private DashRepoSecureFilter filter;
    Result dummy = Results.html();

    @Before
    public void before() {
        session = new TestSession();
        session.put(SESSION_USERNAME_KEY, "foo");

        filter = new DashRepoSecureFilter();
        when(context.getSession()).thenReturn(session);
        when(context.getRequestPath()).thenReturn("/foo");


        when(filterChain.next(context)).thenReturn(dummy);
    }

    @Test
    public void testApiRequestPostLogin() {
        Result result = filter.filter(filterChain, context);
        assertThat(result, is(dummy));
    }
}
