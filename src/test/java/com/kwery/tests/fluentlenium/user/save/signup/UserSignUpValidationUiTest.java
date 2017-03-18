package com.kwery.tests.fluentlenium.user.save.signup;

import com.google.common.collect.ImmutableMap;
import com.kwery.dao.UserDao;
import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.save.FormField;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.fluentlenium.user.save.FormField.*;
import static com.kwery.tests.util.TestUtil.user;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class UserSignUpValidationUiTest extends AbstractUserSignUpSetUpUiTest {
    protected boolean onboardingFlow;

    public UserSignUpValidationUiTest(boolean onboardingFlow) {
        this.onboardingFlow = onboardingFlow;
    }

    @Parameters(name = "Onboarding{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {true},
                {false},
        });
    }

    @Before
    public void before() {
        this.setOnboardingFlow(this.onboardingFlow);
        super.setUp();
    }

    @Test
    public void testPasswordMismatch() {
        User user = user();
        Map<FormField, String> map = ImmutableMap.of(
                password, user.getPassword(),
                confirmPassword, user().getPassword()
        );
        page.saveUser(map);
        page.assertNonEmptyValidationMessage(confirmPassword);
        assertEmptyUsersTable();
    }

    @Test
    public void testEmailField() {
        Map<FormField, String> map = ImmutableMap.of(
                email, RandomStringUtils.randomAlphanumeric(User.EMAIL_MIN, User.EMAIL_MAX)
        );
        page.saveUser(map);
        page.assertNonEmptyValidationMessage(email);
        assertEmptyUsersTable();
    }

    @Test
    public void testEmptyFormFields() {
        page.saveUser(new HashMap<>());
        for (FormField formField : FormField.values()) {
            page.assertNonEmptyValidationMessage(formField);
        }
        assertEmptyUsersTable();
    }

    protected void assertEmptyUsersTable() {
        assertThat(ninjaServerRule.getInjector().getInstance(UserDao.class).list(), hasSize(0));
    }
}
