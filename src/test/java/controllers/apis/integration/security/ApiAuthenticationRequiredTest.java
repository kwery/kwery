package controllers.apis.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import views.ActionResult;

import java.io.IOException;
import java.util.List;

import static conf.Routes.ADD_ADMIN_USER_API;
import static conf.Routes.ADD_DATASOURCE_API;
import static conf.Routes.ADD_SQL_QUERY_API;
import static conf.Routes.ALL_DATASOURCES_API;
import static conf.Routes.INDEX;
import static conf.Routes.LOGIN_API;
import static conf.Routes.MYSQL_DATASOURCE_CONNECTION_TEST_API;
import static conf.Routes.ONGOING_SQL_QUERY_API;
import static conf.Routes.USER;
import static controllers.apis.integration.security.ApiSecurityTestVo.HttpMethod.GET;
import static controllers.apis.integration.security.ApiSecurityTestVo.HttpMethod.POST;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static util.Messages.USER_NOT_LOGGED_IN_M;
import static util.TestUtil.datasource;
import static util.TestUtil.queryRunDto;
import static util.TestUtil.user;
import static views.ActionResult.Status.failure;

public class ApiAuthenticationRequiredTest extends NinjaDocTester {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    protected List<ApiSecurityTestVo> vos;

    @Before
    public void setUpApiAuthenticationRequiredTest() {
        vos = ImmutableList.of(
                new ApiSecurityTestVo(ADD_DATASOURCE_API, true, POST, datasource()),
                new ApiSecurityTestVo(MYSQL_DATASOURCE_CONNECTION_TEST_API, true, POST, datasource()),
                new ApiSecurityTestVo(USER, true, GET, user()),
                new ApiSecurityTestVo(INDEX, false, GET),
                new ApiSecurityTestVo(ADD_ADMIN_USER_API, false, POST, user()),
                new ApiSecurityTestVo(LOGIN_API, false, POST, user()),
                new ApiSecurityTestVo(ADD_SQL_QUERY_API, true, POST, queryRunDto()),
                new ApiSecurityTestVo(ALL_DATASOURCES_API, true, GET),
                new ApiSecurityTestVo(ONGOING_SQL_QUERY_API, true, GET)
        );
    }

    @Test
    public void test() throws IOException {
        for (ApiSecurityTestVo vo : vos) {
            Object params = new Object();

            if (vo.getParams() != null) {
                params = vo.getParams();
            }

            Response response;

            if (vo.getHttpMethod() == POST) {
                response = sayAndMakeRequest(Request.POST().url(testServerUrl().path(vo.getRoute())).contentTypeApplicationJson().payload(
                        new ObjectMapper().writeValueAsString(params)
                ));
            } else {
                response = sayAndMakeRequest(Request.GET().url(testServerUrl().path(vo.getRoute())));
            }

            if (vo.isSecure()) {
                collector.checkThat(response.headers.get("Content-Type"), containsString("application/json"));
                ActionResult result = response.payloadJsonAs(ActionResult.class);
                collector.checkThat(vo.getRoute(), result.getStatus(), is(failure));
                collector.checkThat(vo.getRoute(), result.getMessages(), hasSize(1));
                collector.checkThat(vo.getRoute(), result.getMessages().get(0), is(USER_NOT_LOGGED_IN_M));
            } else {
                collector.checkThat(vo.getRoute(), response.headers.get("Content-Type"), is(not("application/json")));
            }
        }
    }
}
