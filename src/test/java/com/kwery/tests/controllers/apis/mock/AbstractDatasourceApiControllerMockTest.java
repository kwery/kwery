package com.kwery.tests.controllers.apis.mock;

import com.kwery.controllers.apis.DatasourceApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.services.datasource.DatasourceService;
import ninja.validation.Validation;
import org.junit.Before;
import org.mockito.Mock;

import static org.mockito.Mockito.when;

public abstract class AbstractDatasourceApiControllerMockTest extends ControllerMockTest {
    @Mock
    protected DatasourceDao datasourceDao;
    @Mock
    protected Validation validation;
    @Mock
    protected DatasourceService datasourceService;

    protected DatasourceApiController datasourceApiController;

    @Before
    public void before() {
        datasourceApiController = new DatasourceApiController();
        datasourceApiController.setMessages(messages);
        datasourceApiController.setDatasourceDao(datasourceDao);
        datasourceApiController.setDatasourceService(datasourceService);
        when(validation.hasViolations()).thenReturn(false);
    }
}
