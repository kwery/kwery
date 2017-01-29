package com.kwery.tests.fluentlenium.security;

import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.NinjaServerRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class PageAccessAuthenticationRequiredTest extends ChromeFluentTest {
    @Rule
    public NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Test
    public void test() throws IOException {
/*        Set<ClassPath.ClassInfo> classInfos = ClassPath.from(Thread.currentThread().getContextClassLoader()).getTopLevelClassesRecursive("com.kwery.tests.fluentlenium");
        for (ClassPath.ClassInfo classInfo : classInfos) {
            Class clazz = classInfo.load();
            if (FluentPage.class.isAssignableFrom(clazz)) {
                FluentPage page = newInstance(clazz);
                page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);
                if (isUnauthenticated(clazz)) {
                    assertThat(((RepoDashPage)page).isRendered(), is(true));
                } else {
                    await().atMost(TestUtil.TIMEOUT_SECONDS, SECONDS).until($("#loginForm")).isDisplayed();
                }
            }
        }*/
    }

    private boolean isUnauthenticated(Class clazz) {
        Annotation annotation = clazz.getAnnotation(Unauthenticated.class);
        return annotation != null && annotation instanceof Unauthenticated;
    }
}
