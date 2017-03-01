package com.kwery.tests.dao.search;

import com.google.common.collect.ImmutableList;
import com.kwery.dao.search.JobSearchDao;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JobSearchDaoTestFilter {
    @Test
    public void test() {
        String input = "     foo and @*#@(*(*()#*      bar      ";
        List<String> filtered = new JobSearchDao(null).filter(input);
        assertThat(filtered.size(), is(2));
        assertThat(filtered, is(ImmutableList.of("foo", "bar")));
    }
}
