package com.kwery.tests.utils;

import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryConstant;
import com.kwery.utils.KweryUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KweryUtilIsFileWithinLimitsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTrue() throws Exception {
        File file0 = temporaryFolder.newFile();
        TestUtil.writeCsvOfLines(KweryConstant.SQL_QUERY_RESULT_DISPLAY_ROW_LIMIT - 1, file0);
        assertThat(KweryUtil.isFileWithinLimits(file0), is(true));

        File file1 = new File(temporaryFolder.newFolder(), UUID.randomUUID().toString());
        TestUtil.writeFileOfSize(KweryConstant.SQL_QUERY_RESULT_DISPLAY_SIZE_LIMIT - 1024, file1);
        assertThat(KweryUtil.isFileWithinLimits(file1), is(true));
    }

    @Test
    public void testFalse() throws Exception {
        File file0 = temporaryFolder.newFile();
        TestUtil.writeCsvOfLines(KweryConstant.SQL_QUERY_RESULT_DISPLAY_ROW_LIMIT, file0);
        assertThat(KweryUtil.isFileWithinLimits(file0), is(false));

        File file1 = new File(temporaryFolder.newFolder(), UUID.randomUUID().toString());
        TestUtil.writeFileOfSize(KweryConstant.SQL_QUERY_RESULT_DISPLAY_SIZE_LIMIT + 1024, file1);
        assertThat(KweryUtil.isFileWithinLimits(file1), is(false));
    }
}
