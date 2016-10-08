package fluentlenium.utils;

import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.operation.Insert;
import com.ninja_squad.dbsetup.operation.Operation;
import models.User;

import static models.User.COLUMN_ID;
import static models.User.COLUMN_PASSWORD;
import static models.User.COLUMN_USERNAME;
import static models.User.TABLE_DASH_REPO_USER;

//TODO - Requires massive refactoring
public class UserTableUtil {
    protected int rowCount;

    public UserTableUtil() {
        this(1);
    }

    public UserTableUtil(int rowCount) {
        this.rowCount = rowCount;

        this.user0 = new User();
        user0.setId(1);
        user0.setUsername("root");
        user0.setPassword("root");

        this.user1 = new User();
        user1.setId(2);
        user1.setUsername("user2");
        user1.setPassword("password2");
    }

    protected User user0 = new User();
    protected User user1 = new User();

    public Operation insertOperation() {
        Insert.Builder columns = Operations.insertInto(TABLE_DASH_REPO_USER).columns(COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD);
        Insert.Builder values = columns.values(user0.getId(), user0.getUsername(), user0.getPassword());

        if (rowCount == 2) {
             values = values.values(user1.getId(), user1.getUsername(), user1.getPassword());
        }

        return values.build();
    }

    public User firstRow() {
        return user0;
    }

    public User row(int position) {
        if (position == 0) {
            return user0;
        }

        if (position == 1) {
            return user1;
        }

        throw new UnsupportedOperationException("Only two rows in the table");
    }
}
