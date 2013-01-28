package com.knaptus.oss.redis.dictionary;

/**
 * Extracts keyword from the data provided.
 *
 * @author Aditya Bhardwaj
 * @param <T> type of the data
 */
public interface KeywordAndIdExtractor<T> {

    /**
     * Extracts all the keyword in a sentence.
     *
     * @param data
     * @return
     */
    String extractKeywords(T data);

    /**
     * Extracts id from the data that will be used for indexing.
     *
     * @param data
     * @return
     */
    String extractId(T data);
}
