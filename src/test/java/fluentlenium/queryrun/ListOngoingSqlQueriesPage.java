package fluentlenium.queryrun;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

public class ListOngoingSqlQueriesPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        return $("#ongoingQueriesTable").first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/currently-executing";
    }
}
