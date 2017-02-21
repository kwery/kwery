package com.kwery.tests.utils;

import com.kwery.tests.util.TestUtil;
import com.kwery.utils.KweryConstant;
import com.kwery.utils.KweryUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class KweryUtilCanFileBeAttachedTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testTrue() throws IOException {
        File file = new File(temporaryFolder.newFolder(), UUID.randomUUID().toString());
        TestUtil.writeFileOfSize(KweryConstant.SQL_QUERY_RESULT_ATTACHMENT_SIZE_LIMIT - 1024, file);
        assertThat(KweryUtil.canFileBeAttached(file), is(true));
    }

    @Test
    public void testFalse() throws IOException {
        File file = new File(temporaryFolder.newFolder(), UUID.randomUUID().toString());
        TestUtil.writeFileOfSize(KweryConstant.SQL_QUERY_RESULT_ATTACHMENT_SIZE_LIMIT + 1024, file);
        assertThat(KweryUtil.canFileBeAttached(file), is(false));
    }
}
