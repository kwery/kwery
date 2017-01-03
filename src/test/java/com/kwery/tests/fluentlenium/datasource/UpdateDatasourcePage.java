package com.kwery.tests.fluentlenium.datasource;

import com.kwery.models.Datasource.Type;
import org.fluentlenium.core.domain.FluentWebElement;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.kwery.tests.util.Messages.DATASOURCE_UPDATE_SUCCESS_M;
import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.name;

public class UpdateDatasourcePage extends DatasourceAddPage {
    public static final int FIELDS = 5;

    protected int datasourceId;

    @Override
    public String getUrl() {
        return "/#datasource/" + getDatasourceId();
    }

    public void waitForForm(FormField field, String value) {
        await().atMost(30, SECONDS).until(name(field.name())).with("value").startsWith(value);
    }

    public void waitForSuccessMessage(String label, Type type) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".f-success-message").hasText(MessageFormat.format(DATASOURCE_UPDATE_SUCCESS_M, type.name(), label));
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
        find(id("create")).click();
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }
}
