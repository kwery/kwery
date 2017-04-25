package com.kwery.csv;

import au.com.bytecode.opencsv.ResultSetHelperService;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KweryResultSetHelperService extends ResultSetHelperService {
    @Override
    public String[] getColumnNames(ResultSet rs) throws SQLException {
        List<String> names = new ArrayList<String>();
        ResultSetMetaData metadata = rs.getMetaData();

        for (int i = 0; i < metadata.getColumnCount(); i++) {
            names.add(metadata.getColumnLabel(i+1));
        }

        String[] nameArray = new String[names.size()];
        return names.toArray(nameArray);
    }
}
