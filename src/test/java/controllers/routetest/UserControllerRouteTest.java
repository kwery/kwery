package controllers.routetest;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static conf.Routes.ONBOARDING_ADD_ADMIN_USER;

public class UserControllerRouteTest extends RouteTest {
    @Test
    public void testCreateAdminUser() {
        this.setUrl(ONBOARDING_ADD_ADMIN_USER);
        this.setPostParams(
                ImmutableMap.of(
                        "username", "foo",
                        "password", "password"
                ));
        this.assertPostJson();
    }
}
