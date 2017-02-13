package com.kwery.utils;

import com.kwery.models.JobLabelModel;

import java.io.*;
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

    public static boolean fileHasLinesLesserThan(File file, int limit) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines = lines + 1;
                if (lines >= limit) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isFileWithinLimits(File file) throws IOException {
        return ((file.length() < KweryConstant.SQL_QUERY_RESULT_DISPLAY_SIZE_LIMIT)
                && (KweryUtil.fileHasLinesLesserThan(file, KweryConstant.SQL_QUERY_RESULT_DISPLAY_ROW_LIMIT)));
    }

    public static boolean canFileBeAttached(File resultFile) {
        return resultFile.length() < KweryConstant.SQL_QUERY_RESULT_ATTACHMENT_SIZE_LIMIT;
    }
}
