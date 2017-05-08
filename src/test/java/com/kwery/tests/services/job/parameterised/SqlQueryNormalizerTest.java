package com.kwery.tests.services.job.parameterised;

import com.kwery.services.job.parameterised.SqlQueryNormalizer;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryNormalizerTest {
    @Test
    public void test() {
        String q = "select * from foo where bar > :moo";
        SqlQueryNormalizer n = new SqlQueryNormalizer(q);
        assertThat(n.normalise(), is("select * from foo where bar > ?"));
    }

    @Test
    public void testMultiple() {
        String q = "select * from foo where bar > :moo and roo > :cho";
        SqlQueryNormalizer n = new SqlQueryNormalizer(q);
        assertThat(n.normalise(), is("select * from foo where bar > ? and roo > ?"));
    }
}
