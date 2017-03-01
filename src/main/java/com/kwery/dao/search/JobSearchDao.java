package com.kwery.dao.search;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.kwery.models.JobModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class JobSearchDao {
    private final Provider<EntityManager> entityManagerProvider;

    @Inject
    public JobSearchDao(Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Transactional
    public List search(SearchFilter filter) {
        if (filter.getFirstResult() != null) {
            Preconditions.checkArgument(filter.getMaxResults() != null, "Result count cannot be null when page number is set");
        }

        FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManagerProvider.get());
        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(JobModel.class).get();

        List<org.apache.lucene.search.Query> queries = new LinkedList<>();

        //Filtering is done to avoid this problem http://stackoverflow.com/questions/13765698/getting-error-on-a-specific-query
        //TODO - Hacky, figure out a better way to achieve this
        for (String s : filter(filter.getPhrase())) {
            org.apache.lucene.search.Query luceneQuery = qb
                    .keyword()
                    .onField("title").boostedTo(10.0f)
                    .matching(s)
                    .createQuery();
            queries.add(luceneQuery);

            luceneQuery = qb
                    .keyword()
                    .onField("name").boostedTo(5.0f)
                    .matching(s)
                    .createQuery();
            queries.add(luceneQuery);

            luceneQuery = qb
                    .keyword()
                    .onField("sqlQueries.datasource.label").boostedTo(0.1f)
                    .matching(s)
                    .createQuery();
            queries.add(luceneQuery);

            luceneQuery = qb
                    .keyword()
                    .onFields("labels.label", "sqlQueries.label", "sqlQueries.title")
                    .matching(s)
                    .createQuery();
            queries.add(luceneQuery);
        }

        BooleanJunction booleanQueryBuilder = qb.bool();

        for (org.apache.lucene.search.Query query : queries) {
            booleanQueryBuilder.should(query);
        }

        org.hibernate.search.jpa.FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(booleanQueryBuilder.createQuery(), JobModel.class);

        if (filter.getFirstResult() != null) {
            jpaQuery
                    .setFirstResult(filter.getFirstResult())
                    .setMaxResults(filter.getMaxResults());
        } else if (filter.getMaxResults() != null) {
            jpaQuery.setMaxResults(filter.getMaxResults());
        }

        return jpaQuery.getResultList();
    }

    @VisibleForTesting
    public List<String> filter(String phrase) {
        String[] split = phrase.split("\\s+");

        List<String> filtered = new LinkedList<>();

        Analyzer analyzer = new StandardAnalyzer();

        for (String s : split) {
            try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(s))) {
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    filtered.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return filtered;
    }
}
