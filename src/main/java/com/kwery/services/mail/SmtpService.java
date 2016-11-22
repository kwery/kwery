package com.kwery.services.mail;

import com.google.inject.Inject;
import com.kwery.dao.SmtpDetailDao;
import com.kwery.models.SmtpDetail;

import java.util.List;

public class SmtpService {
    protected final SmtpDetailDao smtpDetailDao;

    @Inject
    public SmtpService(SmtpDetailDao smtpDetailDao) {
        this.smtpDetailDao = smtpDetailDao;
    }

    public void save(SmtpDetail smtpDetail) throws SmtpConfigurationAlreadyPresentException, MultipleSmtpConfigurationFoundException {
        try {
            SmtpDetail existing = getSmtpConfiguration();
            if (!existing.getId().equals(smtpDetail.getId())) {
                throw new SmtpConfigurationAlreadyPresentException();
            }
        } catch (SmtpConfigurationNotFoundException e) {
        }
        smtpDetailDao.save(smtpDetail);
    }

    public SmtpDetail getSmtpConfiguration() throws SmtpConfigurationNotFoundException, MultipleSmtpConfigurationFoundException {
        List<SmtpDetail> details = smtpDetailDao.get();
        if (details.isEmpty()) {
            throw new SmtpConfigurationNotFoundException();
        }

        if (details.size() > 1) {
            throw new MultipleSmtpConfigurationFoundException();
        }

        return details.get(0);
    }
}
