package controllers.apis.integration.sqlqueryapicontroller;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import controllers.apis.SqlQueryApiController;
import dao.DatasourceDao;
import dtos.SqlQueryDto;
import fluentlenium.utils.DbUtil;
import models.Datasource;
import models.SqlQuery;
import org.junit.Before;
import org.junit.Test;
import util.RepoDashDaoTestBase;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static models.Datasource.COLUMN_ID;
import static models.Datasource.COLUMN_LABEL;
import static models.Datasource.COLUMN_PASSWORD;
import static models.Datasource.COLUMN_PORT;
import static models.Datasource.COLUMN_TYPE;
import static models.Datasource.COLUMN_URL;
import static models.Datasource.COLUMN_USERNAME;
import static models.Datasource.Type.MYSQL;
import static org.junit.Assert.assertThat;

public class SqlQueryApiControllerToSqlQueryModelTest extends RepoDashDaoTestBase {
    protected DatasourceDao datasourceDao;

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
    }

    @Test
    public void test() {
        SqlQueryApiController controller = new SqlQueryApiController();
        controller.setDatasourceDao(datasourceDao);

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

        SqlQuery model = new SqlQuery();
        model.setCronExpression(cronExpression);
        model.setLabel(label);
        model.setQuery(query);
        model.setId(1);
        Datasource datasource = datasourceDao.getById(1);
        model.setDatasource(datasource);

        assertThat(controller.toSqlQueryModel(dto), sameBeanAs(model));
    }
}
