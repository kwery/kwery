package controllers.apis.mock;

import controllers.apis.DatasourceApiController;
import dao.DatasourceDao;
import ninja.validation.Validation;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractDatasourceApiControllerMockTest extends ControllerMockTest {
    @Mock
    protected DatasourceDao datasourceDao;
    @Mock
    protected Validation validation;

    protected DatasourceApiController datasourceApiController;

    @Before
    public void before() {
        datasourceApiController = new DatasourceApiController();
        datasourceApiController.setMessages(messages);
        datasourceApiController.setDatasourceDao(datasourceDao);
        when(validation.hasViolations()).thenReturn(false);
    }
}
