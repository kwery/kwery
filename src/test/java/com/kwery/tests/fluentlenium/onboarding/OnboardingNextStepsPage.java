package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;

import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.className;

public class OnboardingNextStepsPage extends FluentPage implements RepoDashPage {
    public static final int NEXT_STEPS_COUNT = 2;

    @FindBy(className = "f-next-steps")
    protected FluentWebElement nextStepsContainer;

    @Override
    public String getUrl() {
        return "/#onboarding";
    }

    @Override
    public boolean isRendered() {
        await().atMost(30, TimeUnit.SECONDS).until(nextStepsContainer).isDisplayed();
        return true;
    }

    public String nextStepsHeaderText() {
        return $(className("f-next-steps-header")).getText();
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
        return $(".f-add-job").first().isDisplayed();
    }
}
