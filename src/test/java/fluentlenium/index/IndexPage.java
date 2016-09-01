package fluentlenium.index;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.annotation.AjaxElement;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.support.FindBy;
import util.Messages;

import static util.Messages.INSTALLATION_WELCOME_M;

public class IndexPage extends FluentPage implements RepoDashPage {
    @AjaxElement
    @FindBy(id = "heroText")
    protected FluentWebElement heroText;

    @AjaxElement
    @FindBy(id = "createAdminUser")
    protected FluentWebElement nextActionButton;

    @Override
    public String getUrl() {
        return "/";
    }

    @Override
    public boolean isRendered() {
        return heroText.isDisplayed();
    }

    public String expectedHeroText() {
        return INSTALLATION_WELCOME_M;
    }

    public FluentWebElement getHeroText() {
        return heroText;
    }

    public void setHeroText(FluentWebElement heroText) {
        this.heroText = heroText;
    }

    public FluentWebElement getNextActionButton() {
        return nextActionButton;
    }

    public void setNextActionButton(FluentWebElement nextActionButton) {
        this.nextActionButton = nextActionButton;
    }

    public void clickNextActionButton() {
        nextActionButton.click();
    }

    public String expectedNextActionButtonText() {
        return Messages.CREATE_ADMIN_USER_M.toUpperCase();
    }
}
