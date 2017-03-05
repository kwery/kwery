package com.kwery.tests.util;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static junit.framework.TestCase.fail;

public class LoginRule implements TestRule {
    protected User loggedInUser;

    protected NinjaServerRule ninjaServerRule;
    protected FluentTest fluentTest;

    public LoginRule(NinjaServerRule ninjaServerRule, FluentTest fluentTest) {
        this.ninjaServerRule = ninjaServerRule;
        this.fluentTest = fluentTest;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                loggedInUser = TestUtil.user();
                userDbSetUp(loggedInUser);

                UserLoginPage loginPage = fluentTest.newInstance(UserLoginPage.class);
                loginPage.go();

                if (!loginPage.isRendered()) {
                    fail("Login page is not rendered");
                }

                loginPage.submitForm(loggedInUser.getEmail(), loggedInUser.getPassword());

                base.evaluate();
            }
        };
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
