package controllers.fluentlenium;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DashRepoFluentLeniumTestTest {
    @Test
    public void testHtmlClass() {
        assertEquals("Converts a string to class representation", ".foo", new DashRepoFluentLeniumTest().htmlClass("foo"));
    }

    @Test
    public void testHtmlId() {
        assertEquals("Converts a string to id representation", "#foo", new DashRepoFluentLeniumTest().htmlId("foo"));
    }

    @Test
    public void testHtmlClassExpression() {
        assertEquals("Converts a string and html elements to a CSS class expression", ".foo p a", new DashRepoFluentLeniumTest().htmlClassExpression("foo", "p", "a"));
    }

    @Test
    public void testHtmlNamedTextInputExpression() {
        assertEquals("Returns a CSS expression with named text input type", "input[type='text'][name='foo']", new DashRepoFluentLeniumTest().htmlNamedTextInputExpression("foo"));
    }

    @Test
    public void testHtmlNamedPasswordInputExpression() {
        assertEquals("Returns a CSS expression with named password input type", "input[type='password'][name='foo']", new DashRepoFluentLeniumTest().htmlNamedPasswordInputExpression("foo"));
    }

}
