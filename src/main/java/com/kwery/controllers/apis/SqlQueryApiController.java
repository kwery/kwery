package com.kwery.controllers.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.kwery.dao.SqlQueryDao;
import com.kwery.dao.SqlQueryExecutionDao;
import com.kwery.dtos.SqlQueryExecutionDto;
import com.kwery.dtos.SqlQueryExecutionListDto;
import com.kwery.dtos.SqlQueryExecutionListFilterDto;
import com.kwery.filters.DashRepoSecureFilter;
import com.kwery.models.SqlQueryExecutionModel;
import com.kwery.models.SqlQueryModel;
import com.kwery.services.scheduler.SqlQueryExecutionSearchFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.params.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;
import static com.kwery.models.SqlQueryExecutionModel.Status.ONGOING;
import static ninja.Results.json;

public class SqlQueryApiController {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DISPLAY_DATE_FORMAT = "EEE MMM dd yyyy HH:mm";
    public static final String FILTER_DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @Inject
    private SqlQueryDao sqlQueryDao;

    @Inject
    private SqlQueryExecutionDao sqlQueryExecutionDao;

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueries() {
        if (logger.isTraceEnabled()) logger.trace(">");
        List<SqlQueryModel> sqlQueries = sqlQueryDao.getAll();
        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(sqlQueries);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result executingSqlQueries() {
        if (logger.isTraceEnabled()) logger.trace(">");
        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setStatuses(ImmutableList.of(ONGOING));
        List<SqlQueryExecutionModel> models = sqlQueryExecutionDao.filter(filter);
        Collections.sort(models, Comparator.comparing(SqlQueryExecutionModel::getExecutionStart));

        List<SqlQueryExecutionDto> dtos = new ArrayList<>(models.size());

        for (SqlQueryExecutionModel model : models) {
            dtos.add(from(model));
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(dtos);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result listSqlQueryExecution(@PathParam("sqlQueryId") Integer sqlQueryId, SqlQueryExecutionListFilterDto filterDto) throws ParseException {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryExecutionSearchFilter dbFilter = new SqlQueryExecutionSearchFilter();
        dbFilter.setSqlQueryId(sqlQueryId);

        dbFilter.setPageNumber(filterDto.getPageNumber());
        dbFilter.setResultCount(filterDto.getResultCount());

        if (!"".equals(nullToEmpty(filterDto.getExecutionStartStart()))) {
            dbFilter.setExecutionStartStart(getTime(filterDto.getExecutionStartStart()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionStartEnd()))) {
            dbFilter.setExecutionStartEnd(getTime(filterDto.getExecutionStartEnd()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionEndStart()))) {
            dbFilter.setExecutionEndStart(getTime(filterDto.getExecutionEndStart()));
        }

        if (!"".equals(nullToEmpty(filterDto.getExecutionEndEnd()))) {
            dbFilter.setExecutionEndEnd(getTime(filterDto.getExecutionEndEnd()));
        }

        if (filterDto.getStatuses() != null && filterDto.getStatuses().size() > 0) {
            List<SqlQueryExecutionModel.Status> fromRequest = new ArrayList<>(filterDto.getStatuses().size());
            for (String status : filterDto.getStatuses()) {
                fromRequest.add(SqlQueryExecutionModel.Status.valueOf(status));
            }

            dbFilter.setStatuses(fromRequest);
        }

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(dbFilter);

        Collections.sort(sqlQueryExecutions, Comparator.comparing(SqlQueryExecutionModel::getExecutionStart));

        List<SqlQueryExecutionDto> sqlQueryExecutionDtos = new ArrayList<>(sqlQueryExecutions.size());

        for (SqlQueryExecutionModel sqlQueryExecution : sqlQueryExecutions) {
            sqlQueryExecutionDtos.add(from(sqlQueryExecution));
        }

        SqlQueryModel sqlQuery = sqlQueryDao.getById(sqlQueryId);

        SqlQueryExecutionListDto sqlQueryExecutionListDto = new SqlQueryExecutionListDto();
        sqlQueryExecutionListDto.setSqlQuery(sqlQuery.getQuery());
        sqlQueryExecutionListDto.setSqlQueryExecutionDtos(sqlQueryExecutionDtos);
        sqlQueryExecutionListDto.setTotalCount(sqlQueryExecutionDao.count(dbFilter));

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(sqlQueryExecutionListDto);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result sqlQueryExecutionResult(@PathParam("sqlQueryId") Integer sqlQueryId, @PathParam("sqlQueryExecutionId") String sqlQueryExecutionId) throws IOException {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryExecutionSearchFilter filter = new SqlQueryExecutionSearchFilter();
        filter.setSqlQueryId(sqlQueryId);
        filter.setExecutionId(sqlQueryExecutionId);

        List<List<?>> jsonResult = null;

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.filter(filter);

        if (sqlQueryExecutions.size() == 0) {
            jsonResult = new LinkedList<>(new LinkedList<>());
        } else {
            SqlQueryExecutionModel sqlQueryExecution = sqlQueryExecutions.get(0);

            //TODO - Generic type
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResult = objectMapper.readValue(
                    sqlQueryExecution.getResult(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, List.class)
            );
        }

        if (logger.isTraceEnabled()) logger.trace("<");
        return json().render(jsonResult);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result sqlQuery(@PathParam("sqlQueryId") int id) {
        if (logger.isTraceEnabled()) logger.trace(">");

        SqlQueryModel sqlQuery = sqlQueryDao.getById(id);

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(sqlQuery);
    }

    @FilterWith(DashRepoSecureFilter.class)
    public Result latestSqlQueryExecutions() {
        if (logger.isTraceEnabled()) logger.trace(">");

        List<SqlQueryModel> sqlQueries = sqlQueryDao.getAll();

        List<Integer> sqlQueryIds = new ArrayList<>(sqlQueries.size());

        for (SqlQueryModel sqlQuery : sqlQueries) {
            sqlQueryIds.add(sqlQuery.getId());
        }

        List<SqlQueryExecutionModel> sqlQueryExecutions = sqlQueryExecutionDao.lastSuccessfulExecution(sqlQueryIds);

        List<SqlQueryExecutionDto> dtos = from(sqlQueryExecutions);

        if (logger.isTraceEnabled()) logger.trace("<");

        return json().render(dtos);
    }

    public SqlQueryExecutionDto from(SqlQueryExecutionModel model) {
        SqlQueryExecutionDto dto = new SqlQueryExecutionDto();
        dto.setSqlQueryLabel(model.getSqlQuery().getLabel());
        dto.setDatasourceLabel(model.getSqlQuery().getDatasource().getLabel());
        dto.setSqlQueryExecutionStartTime(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(model.getExecutionStart()));

        Long executionEnd = model.getExecutionEnd();
        if (executionEnd != null) {
            dto.setSqlQueryExecutionEndTime(new SimpleDateFormat(DISPLAY_DATE_FORMAT).format(executionEnd));
        }

        dto.setSqlQueryId(model.getSqlQuery().getId());
        dto.setSqlQueryExecutionId(model.getExecutionId());
        dto.setStatus(model.getStatus().name());
        dto.setResult(model.getResult());

        return dto;
    }

    public List<SqlQueryExecutionDto> from(List<SqlQueryExecutionModel> models) {
        List<SqlQueryExecutionDto> dtos = new ArrayList<>(models.size());

        for (SqlQueryExecutionModel model : models) {
            dtos.add(from(model));
        }

        return dtos;
    }

    private long getTime(String date) throws ParseException {
        return new SimpleDateFormat(FILTER_DATE_FORMAT).parse(date).getTime();
    }
}
