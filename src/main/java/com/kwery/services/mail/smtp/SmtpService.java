package com.kwery.services.mail.smtp;

import com.google.inject.Inject;
import com.kwery.dao.SmtpConfigurationDao;
import com.kwery.models.SmtpConfiguration;

import java.util.List;

public class SmtpService {
    protected final SmtpConfigurationDao smtpConfigurationDao;

    @Inject
    public SmtpService(SmtpConfigurationDao smtpConfigurationDao) {
        this.smtpConfigurationDao = smtpConfigurationDao;
    }

    public void save(SmtpConfiguration smtpConfiguration) throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        try {
            SmtpConfiguration existing = getSmtpConfiguration();
            if (!existing.getId().equals(smtpConfiguration.getId())) {
                throw new SmtpConfigurationAlreadyPresentException();
            }
        } catch (SmtpConfigurationNotFoundException e) {
        }
        smtpConfigurationDao.save(smtpConfiguration);
    }

    public SmtpConfiguration getSmtpConfiguration() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        List<SmtpConfiguration> details = smtpConfigurationDao.get();
        if (details.isEmpty()) {
            throw new SmtpConfigurationNotFoundException();
        }

        if (details.size() > 1) {
            throw new MultipleSmtpConfigurationFoundException();
        }

        return details.get(0);
    }
}
