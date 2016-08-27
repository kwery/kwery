package controllers.apis.integration.datasourceapicontroller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import models.Datasource;
import org.junit.Test;

import java.io.IOException;

import static controllers.util.Messages.LABEL_VALIDATION_M;
import static controllers.util.Messages.URL_VALIDATION_M;
import static controllers.util.Messages.USERNAME_VALIDATION_M;

public class AddDatasourceValidationTest extends DatasoureApiControllerTest {
    @Test
    public void testNull() throws IOException {
        Datasource invalid = new Datasource();
        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addDatasourceApi, invalid)),
                ImmutableMap.of(
                        "url", ImmutableList.of(URL_VALIDATION_M),
                        "label", ImmutableList.of(LABEL_VALIDATION_M),
                        "username", ImmutableList.of(USERNAME_VALIDATION_M)
                )
        );
    }

    @Test
    public void testEmpty() throws IOException {
        Datasource invalid = new Datasource();
        invalid.setUrl("");
        invalid.setLabel("");
        invalid.setUsername("");

        assertFailure(
                actionResult(ninjaTestBrowser.postJson(addDatasourceApi, invalid)),
                ImmutableMap.of(
                        "url", ImmutableList.of(URL_VALIDATION_M),
                        "label", ImmutableList.of(LABEL_VALIDATION_M),
                        "username", ImmutableList.of(USERNAME_VALIDATION_M)
                )
        );
    }
}
