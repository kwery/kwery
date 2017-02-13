package com.kwery.tests.utils;

import com.google.common.collect.ImmutableList;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KweryUtilFileHasLinesGreaterThanTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTrue() throws Exception {
        List<String[]> fileContent = ImmutableList.of(
                new String[]{"foo"},
                new String[]{"bar"},
                new String[]{"moo"}
        );

        File file = temporaryFolder.newFile();

        TestUtil.writeCsv(fileContent, file);

        assertThat(KweryUtil.fileHasLinesLesserThan(file, 1), is(false));
        assertThat(KweryUtil.fileHasLinesLesserThan(file, 2), is(false));
        assertThat(KweryUtil.fileHasLinesLesserThan(file, 3), is(false));
        assertThat(KweryUtil.fileHasLinesLesserThan(file, 4), is(true));
        assertThat(KweryUtil.fileHasLinesLesserThan(file, 5), is(true));
    }
}