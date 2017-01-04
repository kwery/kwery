package com.kwery.tests.services;

import com.google.common.collect.ImmutableList;
import com.kwery.services.scheduler.JsonToCsvConverter;
import org.junit.Test;

import java.io.IOException;

import static com.kwery.tests.util.TestUtil.toJson;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonToCsvConverterTest {
    @Test
    public void test() throws IOException {
        String json = toJson(
                ImmutableList.of(
                        ImmutableList.of("c0", "c1"),
                        ImmutableList.of("v0", "v1")
                )
        );

        String csv = new JsonToCsvConverter().convert(json);

        String expected = "\"c0\",\"c1\"\n\"v0\",\"v1\"\n";

        assertThat(csv, is(expected));
    }
}
