package com.kwery.tests;

import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Test;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

public class ThymeleafTest extends RepoDashTestBase {
    @Test
    public void setUp() {
        ITemplateEngine engine = getInstance(ITemplateEngine.class);
        Context context = new Context();
        System.out.println(engine.process("foo", context));
    }
}
