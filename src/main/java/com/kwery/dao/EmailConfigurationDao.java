package com.kwery.dao;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.EmailConfiguration;
import com.kwery.services.EmptyFromEmailException;
import com.kwery.services.mail.EmailValidator;
import com.kwery.services.mail.InvalidEmailException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class EmailConfigurationDao {
    protected final Provider<EntityManager> entityManagerProvider;
    protected final EmailValidator emailValidator;

    @Inject
    public EmailConfigurationDao(Provider<EntityManager> entityManagerProvider, EmailValidator emailValidator) {
        this.entityManagerProvider = entityManagerProvider;
        this.emailValidator = emailValidator;
    }

    /**
     * @throws InvalidEmailException if any of the emails are found to be invalid.
     * @throws com.kwery.services.EmptyFromEmailException if from email is empty.
     */
    @Transactional
    public synchronized void save(EmailConfiguration emailConfiguration) throws InvalidEmailException {
        EntityManager e = entityManagerProvider.get();

        sanitiseEmails(emailConfiguration);

        if ("".equals(emailConfiguration.getFrom())) {
            throw new EmptyFromEmailException();
        }

        List<String> invalids = validateEmails(emailConfiguration);

        if (!invalids.isEmpty()) {
            throw new InvalidEmailException(invalids);
        }

        if (emailConfiguration.getId() != null && emailConfiguration.getId() > 0) {
            e.merge(emailConfiguration);
        } else {
            e.persist(emailConfiguration);
        }

        e.flush();
    }

    protected void sanitiseEmails(EmailConfiguration config) {
        config.setBcc(emailValidator.emailCsvManipulation(config.getBcc()));
        //These cannot be CSV, but we still use this method as it serves the purpose of cleaning up spaces etc
        config.setFrom(emailValidator.emailCsvManipulation(config.getFrom()));
        config.setReplyTo(emailValidator.emailCsvManipulation(config.getReplyTo()));
    }

    protected List<String> validateEmails(EmailConfiguration config) {
        List<String> invalids = new LinkedList<>();
        if (!"".equals(config.getBcc())) {
            invalids.addAll(emailValidator.filterInvalidEmails(Splitter.on(',').splitToList(config.getBcc())));
        }

        if (!"".equals(config.getFrom())) {
            invalids.addAll(emailValidator.filterInvalidEmails(ImmutableList.of(config.getFrom())));
        }

        if (!"".equals(config.getReplyTo())) {
            invalids.addAll(emailValidator.filterInvalidEmails(ImmutableList.of(config.getReplyTo())));
        }

        return invalids;
    }

    @Transactional
    public EmailConfiguration get() {
        EntityManager e = entityManagerProvider.get();
        CriteriaBuilder cb = e.getCriteriaBuilder();
        CriteriaQuery<EmailConfiguration> cq = cb.createQuery(EmailConfiguration.class);
        Root<EmailConfiguration> root = cq.from(EmailConfiguration.class);
        CriteriaQuery<EmailConfiguration> all = cq.select(root);
        return e.createQuery(all).getSingleResult();
    }

    @Transactional
    public EmailConfiguration get(int id) {
        return entityManagerProvider.get().find(EmailConfiguration.class, id);
    }
}
