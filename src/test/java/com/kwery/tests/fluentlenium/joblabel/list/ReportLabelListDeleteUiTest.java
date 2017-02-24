package com.kwery.tests.fluentlenium.joblabel.list;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.text.MessageFormat;

import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M;

public class ReportLabelListDeleteUiTest extends AbstractReportLabelListUiTest {
    @Test
    public void testFailure() {
        page.clickDelete(0);
        page.assertFailureMessages(ImmutableList.of(
                MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M, parentJobLabelModel.getLabel()),
                MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M, parentJobLabelModel.getLabel())
                )
        );

        page.assertRow(0, page.toMap(parentJobLabelModel));
        page.assertRow(1, page.toMap(jobLabelModel));
    }

    @Test
    public void testSuccess() {
        page.clickDelete(1);
        page.assertSuccessMessage(jobLabelModel.getLabel());
        page.assertRow(0, page.toMap(parentJobLabelModel));
    }
}
