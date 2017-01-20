package com.kwery.utils;

import com.kwery.models.JobLabelModel;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class KweryUtil {
    public static final String CSV_FILE_NAME_DATE_PART = "EEE-MMM-dd";

    public static String fileName(String title, long epoch) {
        return (title + " " + new SimpleDateFormat(CSV_FILE_NAME_DATE_PART).format(epoch)).toLowerCase().trim().replaceAll("\\s+", "-") + ".csv";
    }

    public static Set<Integer> allJobLabelIds(JobLabelModel jobLabelModel) {
        return allJobLabelIds(jobLabelModel, new HashSet<>());
    }

    private static Set<Integer> allJobLabelIds(JobLabelModel jobLabelModel, Set<Integer> ids) {
        if (jobLabelModel == null) {
            return ids;
        } else {
            ids.add(jobLabelModel.getId());
            for (JobLabelModel labelModel : jobLabelModel.getChildLabels()) {
                allJobLabelIds(labelModel, ids);
            }
            return ids;
        }
    }
}
