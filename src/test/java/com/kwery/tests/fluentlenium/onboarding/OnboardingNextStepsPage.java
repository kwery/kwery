package com.kwery.tests.fluentlenium.onboarding;

import com.kwery.tests.util.Messages;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.PageUrl;
import org.fluentlenium.core.domain.FluentList;
import org.fluentlenium.core.domain.FluentWebElement;
import org.fluentlenium.core.hook.wait.Wait;
import org.openqa.selenium.support.FindBy;

import static com.kwery.tests.util.TestUtil.TIMEOUT_SECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.fluentlenium.assertj.FluentLeniumAssertions.assertThat;

@PageUrl("/#onboarding")
@Wait(timeout = TIMEOUT_SECONDS, timeUnit = SECONDS)
public class OnboardingNextStepsPage extends FluentPage {
    public static final int NEXT_STEPS_COUNT = 2;

    @FindBy(css = ".f-next-steps")
    protected FluentWebElement nextSteps;

    @FindBy(css = ".f-next-steps-header")
    protected FluentWebElement nextStepsHeader;

    @FindBy(css = ".f-next-steps ul li")
    protected FluentList<FluentWebElement> nextStepsList;

    @FindBy(css = ".f-add-datasource")
    protected FluentWebElement addDatasourceStep;

    @FindBy(css = ".f-add-job")
    protected FluentWebElement addJobStep;

    @Override
    public void isAt() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(nextSteps).displayed();
    }

    public void nextStepsHeaderText() {
        assertThat(nextStepsHeader).hasText(Messages.NEXT_STEP_HEADER_M);
    }

    public void nextStepsCount() {
        assertThat(nextStepsList).hasSize(NEXT_STEPS_COUNT);
    }

    public void nextStepText(int stepCount, String expectedText) {
        assertThat(nextStepsList.get(stepCount)).hasText(expectedText);
    }

    public void isAddDatasourceNextStepDisplayed(boolean displayed) {
        if (displayed) {
            assertThat(addDatasourceStep).isDisplayed();
        } else {
            assertThat(addDatasourceStep).isNotDisplayed();
        }
    }

    public void waitUntilAddJobStepDisplayed() {
        await().atMost(TIMEOUT_SECONDS, SECONDS).until(addJobStep).displayed();
    }
}
