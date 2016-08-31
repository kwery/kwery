package controllers.apis.integration.datasourceapicontroller;

import com.google.common.collect.ImmutableMap;
import models.Datasource;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.collect.ImmutableList.of;
import static controllers.util.Messages.LABEL_VALIDATION_M;
import static controllers.util.Messages.PORT_VALIDATION_M;
import static controllers.util.Messages.URL_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;

public class AddDatasourceValidationTest extends DatasoureApiControllerTest {
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
                actionResult(ninjaTestBrowser.postJson(addDatasourceApi, invalid)),
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
                actionResult(ninjaTestBrowser.postJson(addDatasourceApi, invalid)),
                MESSAGES
        );
    }
}
