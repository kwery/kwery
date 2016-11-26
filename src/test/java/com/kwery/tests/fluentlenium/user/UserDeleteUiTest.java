package com.kwery.tests.fluentlenium.user;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserDeleteUiTest extends UserListUiTest {
   @Test
   public void test() {
       page.delete(1);
       page.waitForDeleteSuccessMessage(userTableUtil.row(1).getUsername());
       List<List<String>> rows = page.rows();
       assertThat(rows, hasSize(1));
       assertThat(rows.get(0).get(0), is(userTableUtil.row(0).getUsername()));
   }

   @Test
   public void testDeleteYourself() {
       page.delete(0);
       page.waitForDeleteYourselfMessage();
       List<List<String>> rows = page.rows();
       assertThat(rows, hasSize(2));
       assertThat(rows.get(0).get(0), is(userTableUtil.row(0).getUsername()));
       assertThat(rows.get(1).get(0), is(userTableUtil.row(1).getUsername()));
   }
}
