package com.kwery.services.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JsonToHtmlTable {
    public String convert(String json) throws IOException {
        //TODO - Is there a better way to do this, probably use some XML processing library or is it an overkill?
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<List<String>>> typeReference = new TypeReference<List<List<String>>>() {};
        List<List<String>> table = objectMapper.readValue(json, typeReference);

        List<String> htmlTableParts = new LinkedList<>();
        htmlTableParts.add("<table>");

        //Has headers?
        if (!table.isEmpty()) {
            List<String> headers = table.get(0);

            List<String> ths = new ArrayList<>(headers.size());

            htmlTableParts.add("<tr>");

            for (String header : headers) {
                String htmlHeader = "<th>" + header + "</th>";
                ths.add(htmlHeader);
            }

            htmlTableParts.add(Joiner.on("").join(ths));

            htmlTableParts.add("</tr>");

            //Has data?

            if (table.size() > 1) {
                //Skip headers
                for (int i = 1; i < table.size(); ++i) {
                    htmlTableParts.add("<tr>");

                    List<String> row = table.get(i);
                    List<String> tds = new ArrayList<>(row.size());

                    for (String s : row) {
                        tds.add("<td>" + s + "</td>");
                    }
                    htmlTableParts.add(Joiner.on("").join(tds));

                    htmlTableParts.add("</tr>");
                }
            }
        }

        htmlTableParts.add("</table>");

        return Joiner.on("").join(htmlTableParts);
    }
}
