package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource.Type;
import org.fluentlenium.core.domain.FluentWebElement;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.kwery.tests.util.Messages.DATASOURCE_UPDATE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.className;

public class UpdateDatasourcePage extends DatasourceAddPage {
    public static final int FIELDS = 5;

    protected int datasourceId;

    @Override
    public String getUrl() {
        return "/#datasource/" + getDatasourceId();
    }

    public void waitForForm(FormField field, String value) {
        await().atMost(30, SECONDS).until($(field.name())).attribute("value").startsWith(value);
    }

    public void waitForSuccessMessage(String label, Type type) {
        super.waitForSuccessMessage(MessageFormat.format(DATASOURCE_UPDATE_SUCCESS_M, type.name(), label));
    }

    public List<String> formFields() {
        List<String> fields = new ArrayList<>(FIELDS);

        for (FluentWebElement input : $("#addDatasourceForm input")) {
            fields.add(input.value());
        }

        return fields;
    }

    public void fillLabel(String label) {
        $("#label").fill().with(label);
    }

    public void submit() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until($(".save-datasource-f")).clickable();
        find(className("save-datasource-f")).click();
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }
}
