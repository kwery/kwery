package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import org.junit.Test;

public class UserDeleteUiTest extends UserListUiTest {
   @Test
   public void test() {
       for (int i = 0; i < users.size(); ++i) {
           User user = users.get(i);
           if (!loginRule.getLoggedInUser().getEmail().equals(user.getEmail())) {
               page.delete(i);
               page.waitForModalDisappearance();
               page.assertDeleteSuccessMessage(user.getEmail());
           }
       }

       page.assertUserList(0, page.map(loginRule.getLoggedInUser()));
   }

   @Test
   public void testDeleteYourself() {
       for (int i = 0; i < users.size(); ++i) {
           User user = users.get(i);
           if (loginRule.getLoggedInUser().getEmail().equals(user.getEmail())) {
               page.delete(i);
               page.waitForModalDisappearance();
               page.assertDeleteYourselfMessage();
           }

           page.assertUserList(i, page.map(user));
       }
   }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }
}
