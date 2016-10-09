package fluentlenium.datasource;

import fluentlenium.RepoDashFluentLeniumTest;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static models.Datasource.Type.MYSQL;
import static util.Messages.DATASOURCE_UPDATE_SUCCESS_M;

public class UpdateDatasourcePage extends AddDatasourcePage {
    public static final int FIELDS = 5;

    @Override
    public String getUrl() {
        return "/#datasource/1";
    }

    public void waitForForm(String fieldName, String value) {
        await().atMost(30, SECONDS).until(By.name(fieldName)).with("value").startsWith(value);
    }

    public void waitForSuccessMessage(String label) {
        await().atMost(RepoDashFluentLeniumTest.TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(MessageFormat.format(DATASOURCE_UPDATE_SUCCESS_M, MYSQL, label));
    }

    public List<String> formFields() {
        List<String> fields = new ArrayList<>(FIELDS);

        for (FluentWebElement input : $("#addDatasourceForm input")) {
            fields.add(input.getValue());
        }

        return fields;
    }

    public void fillLabel(String label) {
        fill("#label").with(label);
    }

    public void submit() {
        find(By.id("create")).click();
    }
}
