package com.kwery.tests.util;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.login.UserLoginPage;
import com.kwery.tests.fluentlenium.utils.UserTableUtil;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.fluentlenium.adapter.FluentTest;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.kwery.tests.fluentlenium.utils.DbUtil.getDatasource;
import static junit.framework.TestCase.fail;

public class LoginRule implements TestRule {
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
                UserTableUtil userTableUtil = new UserTableUtil();

                new DbSetup(
                        new DataSourceDestination(
                                getDatasource()
                        ),
                        userTableUtil.insertOperation()
                ).launch();

                UserLoginPage loginPage = fluentTest.createPage(UserLoginPage.class);

                loginPage.withDefaultUrl(ninjaServerRule.getServerUrl());
                fluentTest.goTo(loginPage);
                if (!loginPage.isRendered()) {
                    fail("Login page is not rendered");
                }

                User user = userTableUtil.firstRow();

                loginPage.submitForm(user.getUsername(), user.getPassword());
                loginPage.waitForSuccessMessage(user);

                base.evaluate();
            }
        };
    }
}
