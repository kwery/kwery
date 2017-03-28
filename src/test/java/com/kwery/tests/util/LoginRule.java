package com.kwery.tests.util;

import com.kwery.controllers.apis.OnboardingApiController;
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
    protected boolean superUser = false;

    protected NinjaServerRule ninjaServerRule;
    protected FluentTest fluentTest;

    public LoginRule(NinjaServerRule ninjaServerRule, FluentTest fluentTest) {
        this.ninjaServerRule = ninjaServerRule;
        this.fluentTest = fluentTest;
    }

    public LoginRule(NinjaServerRule ninjaServerRule, FluentTest fluentTest, boolean superUser) {
        this.ninjaServerRule = ninjaServerRule;
        this.fluentTest = fluentTest;
        this.superUser = superUser;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                loggedInUser = TestUtil.user();
                loggedInUser.setSuperUser(superUser);

                userDbSetUp(loggedInUser);

                //So that onboarding flows do not kick in automatically on logging in
                System.setProperty(OnboardingApiController.TEST_ONBOARDING_SYSTEM_KEY, "false");

                UserLoginPage loginPage = fluentTest.newInstance(UserLoginPage.class);
                loginPage.go();

                if (!loginPage.isRendered()) {
                    fail("Login page is not rendered");
                }

                loginPage.submitForm(loggedInUser.getEmail(), loggedInUser.getPassword());
                loginPage.waitForModalDisappearance();
                base.evaluate();
            }
        };
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
