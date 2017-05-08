package com.kwery.tests.controllers.apis.integration.jobapicontroller;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.kwery.controllers.apis.JobParameterValidator;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kwery.controllers.apis.JobParameterValidator.Error.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

public class JobParameterValidatorValidateTest extends RepoDashTestBase {
    protected JobParameterValidator jobParameterValidator;

    @Before
    public void setUp() {
        jobParameterValidator = getInstance(JobParameterValidator.class);
    }

    @Test
    public void testEmpty() throws IOException {
        JobDto jobDto = new JobDto();
        jobDto.setParameterCsv("");
        assertThat(jobParameterValidator.validate(jobDto), hasSize(0));
    }

    @Test
    public void testQuoteLineEnding() throws IOException {
        JobDto jobDto = new JobDto();

        String parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                    "header0,",
                    "value0, value1"
                )
        );
        jobDto.setParameterCsv(parameterCsv);
        assertThat(jobParameterValidator.validate(jobDto), hasItem(quoteLineEnding));

        parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                        "header0,     ",
                        "value0, value1"
                )
        );
        jobDto.setParameterCsv(parameterCsv);
        assertThat(jobParameterValidator.validate(jobDto), hasItem(quoteLineEnding));
    }

    @Test
    public void testValueNameCountMismatch() throws IOException {
        JobDto jobDto = new JobDto();
        String parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                        "header0",
                        "value0, value1"
                )
        );
        jobDto.setParameterCsv(parameterCsv);
        assertThat(jobParameterValidator.validate(jobDto), hasItem(valueNameCountMismatch));
    }

    @Test
    public void testParametersNotPresent() throws IOException {
        JobDto jobDto = new JobDto();
        String parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                        "foo, kwery_email",
                        "value0,"
                )
        );
        jobDto.setParameterCsv(parameterCsv);

        List<SqlQueryDto> dtos = new ArrayList<>(2);

        SqlQueryDto sqlQueryDto0 = new SqlQueryDto();
        sqlQueryDto0.setQuery("select * from table where id = :foo and value = :bar");

        dtos.add(sqlQueryDto0);

        SqlQueryDto sqlQueryDto1 = new SqlQueryDto();
        sqlQueryDto1.setQuery("select * from table where id = :bar");

        dtos.add(sqlQueryDto1);

        jobDto.setSqlQueries(dtos);

        assertThat(jobParameterValidator.validate(jobDto), hasItem(parametersNotPresent));
    }

    @Test
    public void testExtraParametersPresent() throws IOException {
        JobDto jobDto = new JobDto();
        String parameterCsv = Joiner.on(System.lineSeparator()).join(
                ImmutableList.of(
                        "foo, bar, kwery_email",
                        "value0, value1, foo@goo.com"
                )
        );
        jobDto.setParameterCsv(parameterCsv);

        List<SqlQueryDto> dtos = new ArrayList<>(2);

        SqlQueryDto sqlQueryDto0 = new SqlQueryDto();
        sqlQueryDto0.setQuery("select * from table where id = :foo");

        dtos.add(sqlQueryDto0);

        SqlQueryDto sqlQueryDto1 = new SqlQueryDto();
        sqlQueryDto1.setQuery("select * from table where id = :foo");

        dtos.add(sqlQueryDto1);

        jobDto.setSqlQueries(dtos);

        assertThat(jobParameterValidator.validate(jobDto), hasItem(extraParametersPresent));
    }
}
