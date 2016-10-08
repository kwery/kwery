package dao.userdao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.UserDao;
import fluentlenium.utils.DbUtil;
import fluentlenium.utils.UserTableUtil;
import models.User;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.util.List;

import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class UserDaoListTest extends RepoDashDaoTestBase {
    protected UserDao userDao;
    protected UserTableUtil userTableUtil;

    @Before
    public void setUpUserDaoListTest() {
        userTableUtil = new UserTableUtil(2);

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation()
                )
        );
        dbSetup.launch();

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() {
        List<User> users = userDao.list();
        assertThat(users, hasSize(2));

        assertThat(users.get(0), sameBeanAs(userTableUtil.row(0)));
        assertThat(users.get(1), sameBeanAs(userTableUtil.row(1)));
    }
}
