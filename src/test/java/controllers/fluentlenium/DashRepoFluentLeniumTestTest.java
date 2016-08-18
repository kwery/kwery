package controllers.fluentlenium;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DashRepoFluentLeniumTestTest {
    @Test
    public void testHtmlClass() {
        assertEquals("Converts a string to class representation", ".foo", new FluentLeniumTest().htmlClass("foo"));
    }

    @Test
    public void testHtmlId() {
        assertEquals("Converts a string to id representation", "#foo", new FluentLeniumTest().htmlId("foo"));
    }

    @Test
    public void testHtmlClassExpression() {
        assertEquals("Converts a string and html elements to a CSS class expression", ".foo p a", new FluentLeniumTest().htmlClassExpression("foo", "p", "a"));
    }

    @Test
    public void testHtmlNamedTextInputExpression() {
        assertEquals("Returns a CSS expression with named text input type", "input[type='text'][name='foo']", new FluentLeniumTest().htmlNamedTextInputExpression("foo"));
    }

    @Test
    public void testHtmlNamedPasswordInputExpression() {
        assertEquals("Returns a CSS expression with named password input type", "input[type='password'][name='foo']", new FluentLeniumTest().htmlNamedPasswordInputExpression("foo"));
    }

}
