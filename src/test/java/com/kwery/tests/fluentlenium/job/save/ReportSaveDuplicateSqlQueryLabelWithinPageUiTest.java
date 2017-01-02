package com.kwery.tests.fluentlenium.job.save;

import com.google.common.collect.ImmutableMap;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.tests.fluentlenium.job.*;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.Messages;
import com.kwery.tests.util.NinjaServerRule;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import java.util.ArrayList;
import java.util.Map;

import static com.kwery.tests.fluentlenium.utils.DbUtil.datasourceDbSetup;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertThat;
import static org.junit.rules.RuleChain.outerRule;

public class ReportSaveDuplicateSqlQueryLabelWithinPageUiTest extends ChromeFluentTest {
    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    protected com.kwery.tests.fluentlenium.job.ReportSavePage page;

    protected JobDto jobDto;

    protected Datasource datasource;

    @Before
    public void setUpReportSaveSuccessUiTest() {
        datasource = datasource();

        datasourceDbSetup(datasource);

        page = createPage(com.kwery.tests.fluentlenium.job.ReportSavePage.class);
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
            sqlQueryDto.setLabel("testLabel");

            jobDto.getSqlQueries().add(sqlQueryDto);
        }
    }

    @Test
    public void test() throws InterruptedException {
        Map<Integer, String> datasourceIdToLabelMap = ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        );

        page.setDatasourceIdToLabelMap(datasourceIdToLabelMap);

        page.fillAndSubmitReportSaveForm(jobDto);

        assertThat(page.validationMessage(com.kwery.tests.fluentlenium.job.ReportSavePage.SqlQueryFormField.queryLabel, 1), Is.is(Messages.REPORT_SAVE_DUPLICATE_SQL_QUERY_LABEL_ERROR));
    }
}
