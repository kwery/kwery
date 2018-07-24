package com.kwery.tests.controllers.apis.integration.datasourceapicontroller.adddatasource;

import com.google.common.collect.ImmutableMap;
import com.kwery.conf.Routes;
import com.kwery.models.Datasource;
import com.kwery.tests.controllers.apis.integration.userapicontroller.AbstractPostLoginApiTest;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.of;
import static com.kwery.conf.Routes.ADD_DATASOURCE_API;
import static com.kwery.tests.util.Messages.*;

public class AddDatasourceValidationTest extends AbstractPostLoginApiTest {
    public static final ImmutableMap<String, java.util.List<String>> MESSAGES = ImmutableMap.of(
            "url", of(URL_VALIDATION_M),
            "port", of(PORT_VALIDATION_M),
            "label", of(LABEL_VALIDATION_M),
            "username", of(USERNAME_VALIDATION_M)
    );

    @Test
    public void testNull() throws IOException {
        Datasource invalid = new Datasource();
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(ADD_DATASOURCE_API), invalid)),
                MESSAGES
        );
    }

    @Test
    public void testEmpty() throws IOException {
        Datasource invalid = new Datasource();
        invalid.setUrl("");
        invalid.setPort(0);
        invalid.setLabel("");
        invalid.setUsername("");

        assertFailure(
                actionResult(ninjaTestBrowser.postJson(getUrl(Routes.ADD_DATASOURCE_API), invalid)),
                MESSAGES
        );
    }
}
