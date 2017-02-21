package com.kwery.tests.services.kweryversion;

import com.kwery.models.KweryVersionModel;
import com.kwery.services.kweryversion.KweryVersionUpdater;
import com.kwery.tests.fluentlenium.utils.DbTableAsserter.DbTableAsserterBuilder;
import com.kwery.tests.util.RepoDashTestBase;
import org.junit.Test;

import static com.kwery.models.KweryVersionModel.ID_COLUMN;
import static com.kwery.models.KweryVersionModel.KWERY_VERSION_MODEL_TABLE;
import static com.kwery.tests.fluentlenium.utils.DbUtil.kweryVersionTable;

public class KweryVersionUpdaterTest extends RepoDashTestBase {
    @Test
    public void test() throws Exception {
        KweryVersionModel kweryVersionModel = new KweryVersionModel();
        kweryVersionModel.setVersion(KweryVersionUpdater.CURRENT_VERSION);

        new DbTableAsserterBuilder(KWERY_VERSION_MODEL_TABLE, kweryVersionTable(kweryVersionModel)).columnsToIgnore(ID_COLUMN).build().assertTable();
    }
}
