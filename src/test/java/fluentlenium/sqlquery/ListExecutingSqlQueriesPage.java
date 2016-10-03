package fluentlenium.sqlquery;

import fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

public class ListExecutingSqlQueriesPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        return $("#executingSqlQueriesTable").first().isDisplayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/executing";
    }
}
