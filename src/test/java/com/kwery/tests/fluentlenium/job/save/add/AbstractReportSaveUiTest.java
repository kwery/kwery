package com.kwery.tests.fluentlenium.job.save.add;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.kwery.dao.JobDao;
import com.kwery.dtos.JobDto;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.*;
import com.kwery.services.job.JobService;
import com.kwery.tests.fluentlenium.job.save.ReportSavePage;
import com.kwery.tests.util.ChromeFluentTest;
import com.kwery.tests.util.LoginRule;
import com.kwery.tests.util.MysqlDockerRule;
import com.kwery.tests.util.NinjaServerRule;
import org.fluentlenium.core.annotation.Page;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

import java.util.ArrayList;

import static com.kwery.tests.fluentlenium.utils.DbUtil.*;
import static com.kwery.tests.fluentlenium.utils.DbUtil.smtpConfigurationDbSetUp;
import static com.kwery.tests.util.TestUtil.*;
import static junit.framework.TestCase.fail;
import static org.junit.rules.RuleChain.outerRule;

public abstract class AbstractReportSaveUiTest extends ChromeFluentTest {
    protected boolean smtpConfigurationSave = true;
    protected boolean urlConfigurationSave = true;

    protected NinjaServerRule ninjaServerRule = new NinjaServerRule();

    @Rule
    public RuleChain ruleChain = outerRule(ninjaServerRule).around(new LoginRule(ninjaServerRule, this));

    @Rule
    public MysqlDockerRule mysqlDockerRule = new MysqlDockerRule();

    @Page
    protected ReportSavePage page;

    protected JobDto jobDto;

    protected Datasource datasource;

    JobDao jobDao;
    JobModel parentJobModel;

    protected boolean noEmailSetting = false;

    @Before
    public void setUp() {
        datasource = mysqlDockerRule.getMySqlDocker().datasource();
        datasource.setId(dbId());
        datasourceDbSetup(datasource);

        jobDto = jobDtoWithoutId();
        jobDto.setCronExpression("* * * * *");
        jobDto.setSqlQueries(new ArrayList<>(1));
        jobDto.setEmails(ImmutableSet.of("foo@bar.com", "moo@bar.com"));
        jobDto.setJobFailureAlertEmails(ImmutableSet.of("foo@goo.com", "cho@roo.com"));

        for (int i = 0; i < 2; ++i) {
            SqlQueryDto sqlQueryDto = sqlQueryDtoWithoutId();
            sqlQueryDto.setQuery("select * from mysql.user");
            sqlQueryDto.setDatasourceId(datasource.getId());

            if (isNoEmailSetting()) {
                SqlQueryEmailSettingModel sqlQueryEmailSettingModel = sqlQueryEmailSettingModelWithoutId();
                sqlQueryDto.setSqlQueryEmailSetting(sqlQueryEmailSettingModel);
            }

            jobDto.getSqlQueries().add(sqlQueryDto);
        }

        parentJobModel = jobModelWithoutDependents();
        jobDbSetUp(parentJobModel);

        SqlQueryModel parentSqlQueryModel = sqlQueryModel(datasource);
        parentSqlQueryModel.setQuery("select * from mysql.user");
        sqlQueryDbSetUp(parentSqlQueryModel);

        parentJobModel.getSqlQueries().add(parentSqlQueryModel);

        jobSqlQueryDbSetUp(parentJobModel);

        if (smtpConfigurationSave) {
            SmtpConfiguration smtpConfiguration = smtpConfiguration();
            smtpConfigurationDbSetUp(smtpConfiguration);
        }

        if (isUrlConfigurationSave()) {
            domainConfigurationDbSetUp(domainSetting());
        }

        ninjaServerRule.getInjector().getInstance(JobService.class).schedule(parentJobModel.getId());

        jobDao = ninjaServerRule.getInjector().getInstance(JobDao.class);

        goTo(page);

        if (!page.isRendered()) {
            fail("Could not render report save page");
        }

        page.setDatasourceIdToLabelMap(ImmutableMap.of(
                datasource.getId(), datasource.getLabel()
        ));

        page.waitForModalDisappearance();
    }

    public boolean isSmtpConfigurationSave() {
        return smtpConfigurationSave;
    }

    public void setSmtpConfigurationSave(boolean smtpConfigurationSave) {
        this.smtpConfigurationSave = smtpConfigurationSave;
    }

    public boolean isUrlConfigurationSave() {
        return urlConfigurationSave;
    }

    public void setUrlConfigurationSave(boolean urlConfigurationSave) {
        this.urlConfigurationSave = urlConfigurationSave;
    }

    @Override
    public String getBaseUrl() {
        return ninjaServerRule.getServerUrl();
    }

    public boolean isNoEmailSetting() {
        return noEmailSetting;
    }

    public void setNoEmailSetting(boolean noEmailSetting) {
        this.noEmailSetting = noEmailSetting;
    }
}
