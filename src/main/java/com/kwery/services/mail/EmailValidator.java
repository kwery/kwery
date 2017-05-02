package com.kwery.services.mail;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.LinkedList;
import java.util.List;

public class EmailValidator {
    public List<String> filterInvalidEmails(List<String> emails) {
        List<String> invalids = new LinkedList<>();
        for (String email : emails) {
            try {
                new InternetAddress(email, true);
            } catch (AddressException e) {
                invalids.add(email);
            }
        }

        return invalids;
    }

    public String emailCsvManipulation(String emailCsv) {
        return Joiner.on(",").join(Splitter.on(',').trimResults().omitEmptyStrings().split(emailCsv));
    }
}
