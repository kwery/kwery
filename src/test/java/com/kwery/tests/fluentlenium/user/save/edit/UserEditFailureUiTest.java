package com.kwery.tests.fluentlenium.user.save.edit;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.save.SaveUtil;
import com.kwery.tests.util.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static com.kwery.tests.fluentlenium.utils.DbUtil.userDbSetUp;
import static com.kwery.tests.util.Messages.SIGN_UP_FAILURE_MESSAGE_M;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class UserEditFailureUiTest extends AbstractUserEditSetUpUiTest {
    private User existing;

    @Before
    public void setUp() {
        super.setUp();
        existing = TestUtil.user();
        userDbSetUp(existing);
    }

    @Test
    public void test() {
        User user = TestUtil.user();
        user.setEmail(existing.getEmail());
        page.saveUser(SaveUtil.toForm(user));
        page.getActionResultComponent().assertFailureMessage(MessageFormat.format(SIGN_UP_FAILURE_MESSAGE_M, user.getEmail()));
        assertThat(userDao.list(), hasSize(3));
    }
}
