package com.kwery.tests.fluentlenium.user;

import com.kwery.models.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UserDeleteUiTest extends UserListUiTest {
   @Test
   public void test() {
       List<User> userCopy = new ArrayList<>(users);

       for (int i = 0; i < users.size(); ++i) {
           User user = users.get(i);
           if (!"root".equals(user.getUsername())) {
               page.delete(i);
               page.assertDeleteSuccessMessage(user.getUsername());
               userCopy.removeIf(user2 -> user.getId().equals(user2.getId()));
           } else {
               page.assertUserList(i, page.map(user));
           }
       }
   }

   @Test
   public void testDeleteYourself() {
       for (int i = 0; i < users.size(); ++i) {
           User user = users.get(i);
           if ("root".equals(user.getUsername())) {
               page.delete(i);
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
