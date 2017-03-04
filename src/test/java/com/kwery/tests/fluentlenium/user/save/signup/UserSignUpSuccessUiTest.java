package com.kwery.tests.fluentlenium.user.save.signup;

import com.kwery.tests.fluentlenium.user.save.SaveUtil;
import org.junit.Test;

import static com.kwery.tests.util.Messages.SIGN_UP_SUCCESS_MESSAGE_M;
import static com.kwery.tests.util.TestUtil.user;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserSignUpSuccessUiTest extends AbstractUserSignUpSetUpUiTest {
    @Test
    public void test() {
        page.saveUser(SaveUtil.toForm(user()));
        page.getActionResultComponent().assertSuccessMessage(SIGN_UP_SUCCESS_MESSAGE_M);
        assertThat(userDao.list(), hasSize(1));
    }
}
