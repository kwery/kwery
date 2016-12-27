package com.kwery.tests.fluentlenium.utils;

import org.dbunit.dataset.AbstractTable;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

//Fix for http://sourceforge.net/p/dbunit/bugs/347/
public class TableWrapper extends AbstractTable {
    protected ITable table;
    protected ITableMetaData metaData;

    public TableWrapper(ITable table, ITableMetaData metaData) {
        this.table = table;
        this.metaData = metaData;
    }

    @Override
    public ITableMetaData getTableMetaData() {
        return metaData;
    }

    @Override
    public int getRowCount() {
        return table.getRowCount();
    }

    @Override
    public Object getValue(int row, String column) throws DataSetException {
        return table.getValue(row, column);
    }
}
