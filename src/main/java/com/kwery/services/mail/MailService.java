package com.kwery.services.mail;

import com.google.inject.Inject;
import com.kwery.dao.SmtpDetailDao;
import com.kwery.models.SmtpDetail;

import java.util.List;

public class MailService {
    protected final SmtpDetailDao smtpDetailDao;

    @Inject
    public MailService(SmtpDetailDao smtpDetailDao) {
        this.smtpDetailDao = smtpDetailDao;
    }

    public SmtpDetail getSmtpConfiguration() throws MailConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        List<SmtpDetail> details = smtpDetailDao.get();
        if (details.isEmpty()) {
            throw new MailConfigurationNotFoundException();
        }

        if (details.size() > 1) {
            throw new MultipleSmtpConfigurationFoundException();
        }

        return details.get(0);
    }
}
