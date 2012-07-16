package com.intelladept.oss.redis.dictionary;

import org.apache.commons.codec.language.Metaphone;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines interface for dictionaries.
 *
 * @author Aditya Bhardwaj
 */
@Named
public class MetaphoneRedisDictionary implements RedisDictionary {

    private Metaphone metaphone;

    @Inject
    private RedisTemplate stringRedisTemplate;

    /**
     * @param dictionaryName
     * @return
     */
    private RedisZSet<String> getDictionary(String dictionaryName, String metaphoneOfWord) {
        return new DefaultRedisZSet<String>(DICT + "mtphn:" + dictionaryName + ":" + metaphoneOfWord, stringRedisTemplate);
    }

    public MetaphoneRedisDictionary() {
        metaphone = new Metaphone();
    }
    
    @Override
    public void addWord(String dictionaryName, String wordToSave) {
        String metaphoneOfWord = metaphone.metaphone(wordToSave);
        getDictionary(dictionaryName, metaphoneOfWord).add(wordToSave);
    }

    @Override
    public List<String> findWords(String dictionaryName, String prefix) {
        String metaphoneOfWord = metaphone.metaphone(prefix);
        return new ArrayList<String> (getDictionary(dictionaryName, metaphoneOfWord));
    }

    @Override
    public List<String> findWords(String dictionaryName, String prefixToFind, int max) {
        return findWords(dictionaryName, prefixToFind);
    }
}
