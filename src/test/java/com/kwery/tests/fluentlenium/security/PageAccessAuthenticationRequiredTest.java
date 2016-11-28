package com.kwery.tests.fluentlenium.security;

import com.google.common.reflect.ClassPath;
import com.kwery.tests.fluentlenium.RepoDashPage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import com.kwery.tests.util.TestUtil;
import org.fluentlenium.core.FluentPage;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PageAccessAuthenticationRequiredTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Test
    public void test() throws IOException {
        Set<ClassPath.ClassInfo> classInfos = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("com.kwery.tests.fluentlenium");
        for (ClassPath.ClassInfo classInfo : classInfos) {
            Class clazz = classInfo.load();
            if (FluentPage.class.isAssignableFrom(clazz)) {
                FluentPage page = createPage(clazz);
                page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
                if (isUnauthenticated(clazz)) {
                    assertThat(((RepoDashPage)page).isRendered(), is(true));
                } else {
                    await().atMost(TestUtil.TIMEOUT_SECONDS, SECONDS).until($("#loginForm")).isDisplayed();
                }
            }
        }
    }

    private boolean isUnauthenticated(Class clazz) {
        Annotation annotation = clazz.getAnnotation(Unauthenticated.class);
        return annotation != null && annotation instanceof Unauthenticated;
    }
}
