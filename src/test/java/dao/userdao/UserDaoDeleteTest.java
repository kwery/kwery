package dao.userdao;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import dao.UserDao;
import fluentlenium.utils.UserTableUtil;
import models.User;
import org.dbunit.DatabaseUnitException;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import java.io.IOException;
import java.sql.SQLException;

import static com.ninja_squad.dbsetup.Operations.sequenceOf;
import static fluentlenium.utils.DbUtil.assertDbState;
import static fluentlenium.utils.DbUtil.getDatasource;

public class UserDaoDeleteTest extends RepoDashDaoTestBase {
    protected UserTableUtil userTableUtil;
    protected UserDao userDao;

    @Before
    public void setUpUserDaoUpdateTest() {
        userTableUtil = new UserTableUtil(1);

        new DbSetup(
                new DataSourceDestination(getDatasource()),
                sequenceOf(
                        userTableUtil.insertOperation()
                )
        ).launch();

        userDao = getInstance(UserDao.class);
    }

    @Test
    public void test() throws DatabaseUnitException, SQLException, IOException {
        userDao.delete(userTableUtil.row(0).getId());
        assertDbState(User.TABLE_DASH_REPO_USER, "userDaoDeleteTest.xml");
    }
}
