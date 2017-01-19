package com.kwery.tests.fluentlenium.joblabel.list;

import org.junit.Test;

import java.text.MessageFormat;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M;
import static com.kwery.tests.util.Messages.JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class ReportLabelListDeleteUiTest extends AbstractReportLabelListUiTest {
    @Test
    public void test() {
        page.waitForModalDisappearance();
        page.clickDelete(0);
        page.waitForFailureMessageDisplay();
        assertThat(page.errorMessages(), containsInAnyOrder(
                MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_JOB_HAS_LABEL_M, parentJobLabelModel.getLabel()),
                MessageFormat.format(JOBLABELAPICONTROLLER_DELETE_HAS_CHILDREN_M, parentJobLabelModel.getLabel())
        ));
        page.clickDelete(1);
        page.waitForSuccessMessage(jobLabelModel.getLabel());
        assertThat(page.getLabels(), hasSize(1));
        assertThat(page.getLabels(), containsInAnyOrder(parentJobLabelModel.getLabel()));
    }
}
