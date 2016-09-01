package fluentlenium;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FluentLeniumTestTest {
    @Test
    public void testHtmlClass() {
        assertThat(new RepoDashFluentLeniumTest().clsSel("foo"), is(".foo"));
    }

    @Test
    public void testHtmlId() {
        assertThat(new RepoDashFluentLeniumTest().idSel("foo"), is("#foo"));
    }

    @Test
    public void testHtmlClassExpression() {
        assertThat(new RepoDashFluentLeniumTest().clsSel("foo", "p", "a"), is(".foo p a"));
    }

    @Test
    public void testHtmlNamedTextInputExpression() {
        assertThat(new RepoDashFluentLeniumTest().inTxtSel("foo"), is("input[type='text'][name='foo']"));
    }

    @Test
    public void testHtmlNamedPasswordInputExpression() {
        assertThat(new RepoDashFluentLeniumTest().inPwdSel("foo"), is("input[type='password'][name='foo']"));
    }
}
