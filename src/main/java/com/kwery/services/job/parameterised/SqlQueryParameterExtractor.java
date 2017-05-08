package com.kwery.services.job.parameterised;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlQueryParameterExtractor {
    public static final String PARAMETER_REG_EX = ":(\\S+)";

    protected final String query;

    @Inject
    public SqlQueryParameterExtractor(@Assisted String query) {
        this.query = query;
    }

    public List<String> extract() {
        Pattern pattern = Pattern.compile(PARAMETER_REG_EX);
        Matcher matcher = pattern.matcher(query);

        List<String> matches = new LinkedList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        return matches;
    }

    public static void main(String[] args) {
        String query = " :select : * form :foo where bar > :goo ";
        SqlQueryParameterExtractor e = new SqlQueryParameterExtractor(query);
        System.out.println(e.extract());
    }
}
