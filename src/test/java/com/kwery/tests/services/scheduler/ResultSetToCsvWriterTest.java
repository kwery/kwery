package com.kwery.tests.services.scheduler;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.kwery.models.Datasource;
import com.kwery.services.datasource.DatasourceService;
import com.kwery.services.scheduler.ResultSetToCsvWriter;
import com.kwery.tests.util.RepoDashTestBase;
import com.kwery.tests.util.TestUtil;
import com.kwery.utils.CsvWriterFactoryImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.MySQLContainer;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResultSetToCsvWriterTest extends RepoDashTestBase {
    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DatasourceService datasourceService;

    @Before
    public void setUp() {
        datasourceService = getInstance(DatasourceService.class);
    }

    @Test
    public void test() throws Exception {
        Connection connection = datasourceService.connection(TestUtil.datasource(mySQLContainer, Datasource.Type.MYSQL));
        String sql = "select \"foo\", \"bar\"";
        PreparedStatement p = connection.prepareStatement(sql);
        ResultSet resultSet = p.executeQuery();
        File file = temporaryFolder.newFile();
        ResultSetToCsvWriter resultSetToCsvWriter = new ResultSetToCsvWriter(new CsvWriterFactoryImpl(), resultSet, file);
        resultSetToCsvWriter.write();

        String expected = String.join(System.lineSeparator(), ImmutableList.of("\"foo\",\"bar\"", "\"foo\",\"bar\"")) + System.lineSeparator();

        assertThat(Files.toString(file, Charsets.UTF_8), is(expected));
    }
}
