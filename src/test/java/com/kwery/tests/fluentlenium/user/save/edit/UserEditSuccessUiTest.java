package com.kwery.tests.fluentlenium.user.save.edit;

import com.kwery.models.User;
import com.kwery.tests.fluentlenium.user.save.SaveUtil;
import com.kwery.tests.util.TestUtil;
import org.junit.Test;

import static com.kwery.tests.util.Messages.SIGN_UP_UPDATE_SUCCESS_MESSAGE_M;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserEditSuccessUiTest extends AbstractUserEditSetUpUiTest {
    @Test
    public void test() {
        User user = TestUtil.user();
        page.saveUser(SaveUtil.toForm(user));
        page.getActionResultComponent().assertSuccessMessage(SIGN_UP_UPDATE_SUCCESS_MESSAGE_M);
        assertThat(userDao.list(), hasSize(2));
    }
}
