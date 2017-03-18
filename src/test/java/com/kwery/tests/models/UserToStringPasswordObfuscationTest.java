package com.kwery.tests.models;

import com.kwery.models.User;
import com.kwery.tests.util.TestUtil;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class UserToStringPasswordObfuscationTest {
    @Test
    public void test() {
        User user = TestUtil.user();
        assertThat(user.toString(), not(containsString(user.getPassword())));
    }
}
