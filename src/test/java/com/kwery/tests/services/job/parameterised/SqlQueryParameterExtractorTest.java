package com.kwery.tests.services.job.parameterised;

import com.google.common.collect.ImmutableList;
import com.kwery.services.job.parameterised.SqlQueryParameterExtractor;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryParameterExtractorTest {
    @Test
    public void testSingleParameter() {
        String sql = "select * from foo where bar > :moo";
        SqlQueryParameterExtractor extractor = new SqlQueryParameterExtractor(sql);
        assertThat(extractor.extract(), is(ImmutableList.of("moo")));
    }

    @Test
    public void testMultipleParameters() {
        String sql = "select * from foo where bar > :moo and gho = :roo";
        SqlQueryParameterExtractor extractor = new SqlQueryParameterExtractor(sql);
        assertThat(extractor.extract(), is(ImmutableList.of("moo", "roo")));
    }

    @Test
    public void testEmpty() {
        String sql = "select * from foo";
        SqlQueryParameterExtractor extractor = new SqlQueryParameterExtractor(sql);
        assertThat(extractor.extract(), is(ImmutableList.of()));
    }
}
