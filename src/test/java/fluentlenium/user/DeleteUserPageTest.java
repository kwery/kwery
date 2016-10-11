package fluentlenium.user;

import org.junit.Test;

public class DeleteUserPageTest extends ListUserPageTest {
   @Test
   public void test() {
       page.delete(1);
       page.waitForDeleteSuccessMessage(userTableUtil.row(1).getUsername());
   }

   @Test
   public void testDeleteYourself() {
       page.delete(0);
       page.waitForDeleteYourselfMessage();
   }
}
