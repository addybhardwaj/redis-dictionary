package com.knaptus.oss.redis.dictionary;

import java.util.List;

/**
 * Defines a repository which indexes data to enable search capabilities.
 *
 * @author Aditya Bhardwaj
 */
public interface SearchableKeywordDataRepository {


    /**
     * Index the data in the dictionary.
     *
     * @param dictionaryName used to store correlated data together.
     * @param keywordAndIdExtractor allows to extract id and keywords from data provided.
     * @param data data to be indexed.
     * @param <T>
     */
    <T> void indexDataByKeywords(String dictionaryName, KeywordAndIdExtractor<T> keywordAndIdExtractor, T data);

    /**
     * Find ids of data which relate to the phrase provided in the dictionary.
     *
     * @param dictionaryName
     * @param phrase
     * @return
     */
    List<String> findDataByPhrase(String dictionaryName, String phrase);

}
