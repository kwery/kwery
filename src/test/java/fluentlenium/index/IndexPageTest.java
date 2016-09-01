package fluentlenium.index;

import fluentlenium.RepoDashFluentLeniumTest;

import static org.junit.Assert.fail;

public abstract class IndexPageTest extends RepoDashFluentLeniumTest {
    protected IndexPage page;

    public void initPage() {
        page = createPage(IndexPage.class);
        page.setBaseUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Index page is not yet rendered");
        }
    }
}
