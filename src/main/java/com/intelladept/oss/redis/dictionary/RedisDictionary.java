package com.intelladept.oss.redis.dictionary;

import java.util.List;

/**
 * Defines interface for dictionaries.
 *
 * @author Aditya Bhardwaj
 */
public interface RedisDictionary {

    String DICT = "dict:";

    String DEFAULT_DICTIONARY = "dflt";

    /**
     * Adds a word to the dictionary.
     *
     * @param dictionaryName
     * @param wordToSave
     */
    void addWord(String dictionaryName, String wordToSave);

    /**
     * Find words which relate to the provided keyword.
     *
     * @param dictionaryName
     * @param searchKeyword
     * @return
     */
    List<String> findWords(String dictionaryName, String searchKeyword);

    /**
     * Find max number of words which relate to the provided keyword.
     *
     * @param dictionaryName
     * @param searchKeyword
     * @param max
     * @return
     */
    List<String> findWords(String dictionaryName, String searchKeyword, int max);
}
