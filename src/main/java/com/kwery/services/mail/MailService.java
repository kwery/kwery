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

    public SmtpDetail getSmtpConfiguration() throws MailConfigurationNotFoundException {
        List<SmtpDetail> details = smtpDetailDao.get();
        if (details.isEmpty()) {
            throw new MailConfigurationNotFoundException();
        }

        return details.get(0);
    }
}
