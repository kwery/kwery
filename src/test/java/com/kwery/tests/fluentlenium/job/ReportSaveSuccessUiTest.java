package com.kwery.tests.fluentlenium.job;

import com.google.common.collect.ImmutableMap;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.NinjaServerRule;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.ArrayList;
import java.util.Map;

import static com.kwery.models.Datasource.*;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.kwery.tests.util.TestUtil.*;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;
import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveSuccessUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected ReportSavePage page;

    protected JobDto jobDto;

    protected Datasource datasource;

    @Before
    public void setUpReportSaveSuccessUiTest() {
        datasource = datasource();

        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(datasource.getId(), datasource.getLabel(), datasource.getPassword(), datasource.getPort(), MYSQL.name(), datasource.getUrl(), datasource.getUsername())
                                .build()
                )
        ).launch();

        page = createPage(ReportSavePage.class);
        page.withDefaultUrl(ninjaServerRule.getServerUrl()).goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }

        jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
            sqlQueryDto.setQuery("select * from mysql.user");
            sqlQueryDto.setDatasourceId(datasource.getId());

            jobDto.getSqlQueries().add(sqlQueryDto);
        }
    }

    @Test
    public void test() throws InterruptedException {
        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );

        page.submitSaveReportForm(jobDto, datasourceIdToLabelMap);
    }
}
