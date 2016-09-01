package fluentlenium.index;

import fluentlenium.RepoDashFluentLeniumTest;
import org.junit.Before;

import static org.junit.Assert.fail;

public abstract class IndexPageTest extends RepoDashFluentLeniumTest {
    protected IndexPage page;

    @Before
    public void setUpIndexPageTest() {
        page = createPage(IndexPage.class);
        page.withDefaultUrl(getServerAddress());
        goTo(page);
        if (!page.isRendered()) {
            fail("Index page is not yet rendered");
        }
    }
}
