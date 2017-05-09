package com.kwery.tests.services.job.email.reporteamailcreator.withcontent;

import com.google.common.collect.ImmutableList;
import com.kwery.services.job.ReportEmailCreator;
import com.kwery.services.mail.KweryMail;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;

public class ReportEmailCreatorAdditionalEmailTest extends AbstractReportEmailCreatorWithContentTest {
    @Test
    public void test() {
        KweryMail kweryMail = getInstance(ReportEmailCreator.class).create(jobExecutionModel, ImmutableList.of("foo@goo.com", "moo@foo.com"));
        assertThat(kweryMail.getTos(), hasItems("foo@goo.com", "moo@foo.com"));
    }

    @Override
    public boolean getEmptyReportEmailRule() {
        return false;
    }
}
