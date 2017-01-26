package com.kwery.utils;

import com.kwery.models.JobExecutionModel;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;

import java.util.ArrayList;
import java.util.List;

public class ReportUtil {
    public static List<SqlQueryExecutionModel> orderedExecutions(JobExecutionModel jobExecutionModel) {
        List<SqlQueryExecutionModel> executions = new ArrayList<>(jobExecutionModel.getSqlQueryExecutionModels().size());
        for (SqlQueryModel sqlQueryModel : jobExecutionModel.getJobModel().getSqlQueries()) {
            for (SqlQueryExecutionModel sqlQueryExecutionModel : jobExecutionModel.getSqlQueryExecutionModels()) {
                if (sqlQueryModel.getId().equals(sqlQueryExecutionModel.getSqlQuery().getId()))    {
                    executions.add(sqlQueryExecutionModel);
                    break;
                }
            }
        }

        return executions;
    }
}
