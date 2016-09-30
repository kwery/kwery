package fluentlenium.utils;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Operation;
import models.User;

import static models.User.COLUMN_ID;
import static models.User.COLUMN_PASSWORD;
import static models.User.COLUMN_USERNAME;
import static models.User.TABLE_DASH_REPO_USER;

public class UserTableUtil {
    protected User user = new User();

    public UserTableUtil() {
        this.user = new User();
        user.setId(1);
        user.setUsername("root");
        user.setPassword("root");
    }

    public Operation insertOperation() {
        return Operations.insertInto(TABLE_DASH_REPO_USER)
                .columns(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD)
                .values(user.getId(), user.getUsername(), user.getPassword()).build();
    }

    public User firstRow() {
        return user;
    }
}
