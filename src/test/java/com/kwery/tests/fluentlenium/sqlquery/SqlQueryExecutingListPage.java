package com.kwery.tests.fluentlenium.sqlquery;

import com.kwery.tests.fluentlenium.RepoDashPage;
import org.fluentlenium.core.FluentPage;

public class SqlQueryExecutingListPage extends FluentPage implements RepoDashPage {
    @Override
    public boolean isRendered() {
        return $("#executingSqlQueriesTable").first().displayed();
    }

    @Override
    public String getUrl() {
        return "/#sql-query/executing";
    }
}
