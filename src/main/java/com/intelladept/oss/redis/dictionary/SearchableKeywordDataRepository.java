package com.intelladept.oss.redis.dictionary;

import java.util.List;

/**
 * TODO
 *
 * @author Aditya Bhardwaj
 */
public interface SearchableKeywordDataRepository {

    <T> void indexDataByKeywords(String dictionaryName, KeywordAndIdExtractor<T> keywordAndIdExtractor, T data);

    List<String> findDataByPhrase(String dictionaryName, String phrase);

}
