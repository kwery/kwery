package com.kwery.tests.controllers.apis.integration.sqlqueryapicontroller;

import com.kwery.controllers.apis.SqlQueryApiController;
import com.kwery.dao.DatasourceDao;
import com.kwery.dtos.SqlQueryDto;
import com.kwery.models.Datasource;
import com.kwery.models.SqlQueryModel;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import static com.kwery.models.Datasource.COLUMN_ID;
import static com.kwery.models.Datasource.COLUMN_LABEL;
import static com.kwery.models.Datasource.COLUMN_PASSWORD;
import static com.kwery.models.Datasource.COLUMN_PORT;
import static com.kwery.models.Datasource.COLUMN_TYPE;
import static com.kwery.models.Datasource.COLUMN_URL;
import static com.kwery.models.Datasource.COLUMN_USERNAME;
import static com.kwery.models.Datasource.Type.MYSQL;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerToSqlQueryModelTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;
    protected SqlQueryApiController controller;

    @Before
    public void setUpSqlQueryApiControllerToSqlQueryModelTest() {
        new DbSetup(
                new DataSourceDestination(DbUtil.getDatasource()),
                Operations.sequenceOf(
                        insertInto(Datasource.TABLE)
                                .columns(COLUMN_ID, COLUMN_LABEL, COLUMN_PASSWORD, COLUMN_PORT, COLUMN_TYPE, COLUMN_URL, COLUMN_USERNAME)
                                .values(1, "testDatasource0", "password", 3306, MYSQL.name(), "foo.com", "username")
                                .build()
                )
        ).launch();

        datasourceDao = getInstance(DatasourceDao.class);

        controller = new SqlQueryApiController();
        controller.setDatasourceDao(datasourceDao);
    }

    @Test
    public void testWithCronExpression() {
        SqlQueryDto dto = new SqlQueryDto();
        String cronExpression = "*";
        dto.setCronExpression(cronExpression);

        String label = "foo";
        dto.setLabel(label);

        String query = "bar";
        dto.setQuery(query);

        int datasourceId = 1;
        dto.setDatasourceId(datasourceId);

        int id = 1;
        dto.setId(id);

        SqlQueryModel model = new SqlQueryModel();
        model.setCronExpression(cronExpression);
        model.setLabel(label);
        model.setQuery(query);
        model.setId(1);
        Datasource datasource = datasourceDao.getById(1);
        model.setDatasource(datasource);

        assertThat(controller.toSqlQueryModel(dto), sameBeanAs(model));
    }

    @Test
    public void testWithEmptyCronExpression() {
        SqlQueryDto dto = new SqlQueryDto();
        String cronExpression = "";
        dto.setCronExpression(cronExpression);

        String label = "foo";
        dto.setLabel(label);

        String query = "bar";
        dto.setQuery(query);

        int datasourceId = 1;
        dto.setDatasourceId(datasourceId);

        int id = 1;
        dto.setId(id);

        SqlQueryModel model = new SqlQueryModel();
        model.setCronExpression(cronExpression);
        model.setLabel(label);
        model.setQuery(query);
        model.setId(1);
        Datasource datasource = datasourceDao.getById(1);
        model.setDatasource(datasource);

        assertThat(controller.toSqlQueryModel(dto), sameBeanAs(model));
    }

    @Test
    public void testWithNullCronExpression() {
        SqlQueryDto dto = new SqlQueryDto();

        int datasourceId = 1;
        dto.setDatasourceId(datasourceId);

        int id = 1;
        dto.setId(id);

        assertThat("Null cron expression is converted to empty string", controller.toSqlQueryModel(dto).getCronExpression(), is(""));
    }

    @Test
    public void testEmail() {
        SqlQueryDto sqlQueryDto = new SqlQueryDto();
        sqlQueryDto.setRecipientEmailsCsv("foo@getkwery.com,,bar@getkwery.com ,,");

        assertThat(controller.toSqlQueryModel(sqlQueryDto).getRecipientEmails(), containsInAnyOrder("foo@getkwery.com", "bar@getkwery.com"));
    }
}
