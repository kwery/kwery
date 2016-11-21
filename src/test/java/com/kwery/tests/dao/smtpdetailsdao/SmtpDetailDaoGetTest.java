package com.kwery.tests.dao.smtpdetailsdao;

import com.kwery.dao.SmtpDetailsDao;
import com.kwery.models.SmtpDetails;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SmtpDetailDaoGetTest extends RepoDashDaoTestBase {
    protected SmtpDetails smtpDetails;
    protected SmtpDetailsDao smtpDetailsDao;

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        smtpDetails = new SmtpDetails();
        smtpDetails.setId(1);
        smtpDetails.setHost("foo.com");
        smtpDetails.setPort(465);
        smtpDetails.setSsl(true);
        smtpDetails.setUsername("username");
        smtpDetails.setPassword("password");

        DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(SmtpDetails.TABLE_SMTP_DETAILS)
                                .row()
                                .column(SmtpDetails.COLUMN_ID, smtpDetails.getId())
                                .column(SmtpDetails.COLUMN_HOST, smtpDetails.getHost())
                                .column(SmtpDetails.COLUMN_PORT, smtpDetails.getPort())
                                .column(SmtpDetails.COLUMN_SSL, smtpDetails.isSsl())
                                .column(SmtpDetails.COLUMN_USERNAME, smtpDetails.getUsername())
                                .column(SmtpDetails.COLUMN_PASSWORD, smtpDetails.getPassword())
                                .end()
                                .build()
                )
        );

        dbSetup.launch();

        smtpDetailsDao = getInstance(SmtpDetailsDao.class);
    }

    @Test
    public void test() {
        List<SmtpDetails> smtpDetailsList = smtpDetailsDao.get();
        assertThat(smtpDetailsList, hasSize(1));
        assertThat(smtpDetailsList.get(0), theSameBeanAs(smtpDetails));
    }
}
