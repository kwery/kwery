package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

public class OnboardingNextStepsPage extends FluentPage implements RepoDashPage {
    public static final int NEXT_STEPS_COUNT = 2;

    @AjaxElement(timeOutInSeconds = 30)
    @FindBy(className = "f-next-steps")
    protected FluentWebElement nextStepsContainer;

    @Override
    public String getUrl() {
        return "/#onboarding";
    }

    @Override
    public boolean isRendered() {
        return nextStepsContainer.isDisplayed();
    }

    public String nextStepsHeaderText() {
        return $(By.className("f-next-steps-header")).getText();
    }

    public int nextStepsCount() {
        return $(".f-next-steps ul li").size();
    }

    public String nextStepText(int stepCount) {
        return $(".f-next-steps ul li").get(stepCount).getText();
    }

    public boolean isAddDatasourceNextStepVisible() {
        return $(".f-add-datasource").first().isDisplayed();
    }

    public boolean isAddSqlQueryNextStepVisible() {
        return $(".f-add-sql-query").first().isDisplayed();
    }
}
