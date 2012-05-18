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

    void addWord(String type, String wordToSave);

    List<String> findWords(String type, String prefix);

    List<String> findWords(String type, String prefixToFind, int max);
}
