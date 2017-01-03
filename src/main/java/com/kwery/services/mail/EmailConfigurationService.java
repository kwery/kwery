package com.kwery.services.mail;

import com.google.inject.Inject;
import com.kwery.dao.EmailConfigurationDao;
import com.kwery.models.EmailConfiguration;

import java.util.List;

public class EmailConfigurationService {
    protected final EmailConfigurationDao emailConfigurationDao;

    @Inject
    public EmailConfigurationService(EmailConfigurationDao emailConfigurationDao) {
        this.emailConfigurationDao = emailConfigurationDao;
    }

    public void save(EmailConfiguration emailConfiguration) throws MultipleEmailConfigurationException, EmailConfigurationExistsException {
        EmailConfiguration existing = getEmailConfiguration();

        if (existing != null && !existing.getId().equals(emailConfiguration.getId())) {
            throw new EmailConfigurationExistsException();
        }

        emailConfigurationDao.save(emailConfiguration);
    }

    public EmailConfiguration getEmailConfiguration() throws MultipleEmailConfigurationException {
        List<EmailConfiguration> es = emailConfigurationDao.get();

        if (es.isEmpty()) {
            return null;
        } else if (es.size() == 1) {
            return es.get(0);
        } else {
            throw new MultipleEmailConfigurationException();
        }
    }
}
