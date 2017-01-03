package com.kwery.tests.dao.smtpconfigurationdao;

import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SmtpConfigurationDaoGetTest extends RepoDashDaoTestBase {
    protected SmtpConfigurationDao smtpConfigurationDao;

    protected Map<Integer, SmtpConfiguration> idDetailMap = new HashMap<>();

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            SmtpConfiguration smtpConfiguration = new SmtpConfiguration();
            smtpConfiguration.setId(i);
            smtpConfiguration.setHost("foo.com");
            smtpConfiguration.setPort(465);
            smtpConfiguration.setSsl(true);
            smtpConfiguration.setUsername("username");
            smtpConfiguration.setPassword("password");

            idDetailMap.put(i, smtpConfiguration);

            DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                    Operations.sequenceOf(
                            insertInto(SmtpConfiguration.TABLE_SMTP_CONFIGURATION)
                                    .row()
                                    .column(SmtpConfiguration.COLUMN_ID, smtpConfiguration.getId())
                                    .column(SmtpConfiguration.COLUMN_HOST, smtpConfiguration.getHost())
                                    .column(SmtpConfiguration.COLUMN_PORT, smtpConfiguration.getPort())
                                    .column(SmtpConfiguration.COLUMN_SSL, smtpConfiguration.isSsl())
                                    .column(SmtpConfiguration.COLUMN_USERNAME, smtpConfiguration.getUsername())
                                    .column(SmtpConfiguration.COLUMN_PASSWORD, smtpConfiguration.getPassword())
                                    .end()
                                    .build()
                    )
            );

            dbSetup.launch();
        }

        smtpConfigurationDao = getInstance(SmtpConfigurationDao.class);
    }

    @Test
    public void testGet() {
        List<SmtpConfiguration> smtpConfigurationList = smtpConfigurationDao.get();
        assertThat(smtpConfigurationList, hasSize(2));
        assertThat(smtpConfigurationList.get(0) , theSameBeanAs(idDetailMap.get(smtpConfigurationList.get(0).getId())));
        assertThat(smtpConfigurationList.get(1) , theSameBeanAs(idDetailMap.get(smtpConfigurationList.get(1).getId())));
    }

    @Test
    public void testGetById() {
        assertThat(smtpConfigurationDao.get(1), theSameBeanAs(idDetailMap.get(1)));
        assertThat(smtpConfigurationDao.get(2), theSameBeanAs(idDetailMap.get(2)));
    }
}
