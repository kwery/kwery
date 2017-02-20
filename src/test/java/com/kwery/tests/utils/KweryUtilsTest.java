package com.kwery.tests.utils;

import com.kwery.utils.KweryUtil;
import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KweryUtilsTest {
    @Test
    public void testFilename() {
        String expected = "this-is-a-long-title-with-hyphen---and-underscore-_-wed-jan-04.csv";

        String title = "   this is   a   long   title    , with hyphen - and underscore _ ";
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, Calendar.JANUARY, 4);
        long epoch = calendar.getTimeInMillis();

        assertThat(KweryUtil.fileName(title, epoch), is(expected));
    }
}
