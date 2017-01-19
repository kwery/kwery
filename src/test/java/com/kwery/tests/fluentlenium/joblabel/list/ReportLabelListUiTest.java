package com.kwery.tests.fluentlenium.joblabel.list;

import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReportLabelListUiTest extends AbstractReportLabelListUiTest {
    @Test
    public void test() {
        page.waitForModalDisappearance();
        //Trimming to remove the UI padded spaces
        List<String> labels = page.getLabels().stream().map(String::trim).collect(toList());
        assertThat(labels, containsInAnyOrder(jobLabelModel.getLabel(), parentJobLabelModel.getLabel()));
        //Assert labels are shown as a tree
        assertThat(labels.get(0), is(parentJobLabelModel.getLabel()));
        assertThat(labels.get(1), is(jobLabelModel.getLabel()));
    }
}
