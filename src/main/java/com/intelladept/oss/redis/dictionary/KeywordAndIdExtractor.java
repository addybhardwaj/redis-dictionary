package com.intelladept.oss.redis.dictionary;

import java.util.Set;

/**
 * Extracts keyword from the data provided.
 *
 * @author Aditya Bhardwaj
 * @param <T> type of the data
 */
public interface KeywordAndIdExtractor<T> {

    /**
     * Extracts all the keyword. Note: keywords should ideally not have spaces in them.
     *
     * @param data
     * @return
     */
    Set<String> extractKeywords(T data);

    /**
     * Extracts id from the data that will be used for indexing.
     *
     * @param data
     * @return
     */
    String extractId(T data);
}
