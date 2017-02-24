package com.kwery.services.search;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobModel;
import com.kwery.utils.ServiceStartUpOrderConstant;
import ninja.lifecycle.Start;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

@Singleton
public class SearchIndexer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public SearchIndexer (Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Start(order = ServiceStartUpOrderConstant.SEARCH_INDEXER_ORDER)
    @Transactional
    public void index() {
        logger.info("Search indexing - start");
        try {
            Search.getFullTextEntityManager(entityManagerProvider.get()).createIndexer(JobModel.class).startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("Search indexing - end");
    }
}
