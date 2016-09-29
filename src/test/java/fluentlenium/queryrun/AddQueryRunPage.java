package fluentlenium.queryrun;

import com.google.common.collect.ImmutableList;
import dtos.SqlQueryDto;
import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static fluentlenium.RepoDashFluentLeniumTest.TIMEOUT_SECONDS;
import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static util.Messages.QUERY_RUN_ADDITION_FAILURE_M;
import static util.Messages.QUERY_RUN_ADDITION_SUCCESS_M;

public class AddQueryRunPage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "queryRunForm")
    protected FluentWebElement form;

    public void submitForm(SqlQueryDto dto) {
        fill("input").with(dto.getQuery(), dto.getCronExpression(), dto.getLabel());
        fillSelect("#datasourceId").withIndex(0);
        click("#create");
    }

    public void submitForm() {
        click("#create");
    }

    public void waitForSuccessMessage() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_info p").hasText(QUERY_RUN_ADDITION_SUCCESS_M);
    }

    public void waitForDuplicateLabelMessage(String label) {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(".isa_error p").hasText(format(QUERY_RUN_ADDITION_FAILURE_M, label));
    }

    public List<String> validationMessages() {
        return ImmutableList.of(
                $("#query-error").getText(),
                $("#cronExpression-error").getText(),
                $("#label-error").getText()
        );
    }

    @Override
    public boolean isRendered() {
        return form.isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/add";
    }
}
