package com.kwery.tests.models;

import com.kwery.models.User;
import org.junit.Test;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class UserToStringPasswordObfuscationTest {
    @Test
    public void test() {
        User user = new User();
        user.setId(1);
        user.setUsername("name");
        user.setPassword("secret");

        assertThat(user.toString(), not(containsString(user.getPassword())));
    }
}
