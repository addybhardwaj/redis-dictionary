package com.intelladept.oss.redis.dictionary;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisZSet;
import org.springframework.data.redis.support.collections.RedisZSet;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implements Word prefix dictionary for auto completion.
 *
 * @author Aditya Bhardwaj
 */
@Named
public class RedisWordCompletionDictionary implements RedisDictionary {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisWordCompletionDictionary.class);

    private static final String END_TOKEN = "*";

    private static final int MAX_COUNT = 50;

    private static final int MAX_TRANS_UNIT = 50;

    @Inject
    private RedisTemplate stringRedisTemplate;


    /**
     * TODO: cache the zset objects and lazily initial them.
     * 
     * @param dictionaryName
     * @return
     */
    private RedisZSet<String> getDictionary(String dictionaryName) {
        return new DefaultRedisZSet<String>(DICT + "compl:" + dictionaryName, stringRedisTemplate);
    }
    

    @Override
    public void addWord(String dictionaryName, final String wordToSave) {
        Validate.notNull(wordToSave);
        String word = wordToSave.toLowerCase();

        String prefix = null;
        //Add all the variants
        for (int i = 1; i < word.length(); i++) {
            prefix = word.substring(0, i);
            getDictionary(dictionaryName).add(prefix, 0);
            LOGGER.debug("Added prefix [{}]", prefix);
        }
        //Add the full word with End prefix to identify full word
        getDictionary(dictionaryName).add(word + END_TOKEN, 0);
        LOGGER.debug("Added word ** [{}]", word);
    }


    @Override
    public List<String> findWords(String dictionaryName, String prefix) {
        return findWords(dictionaryName, prefix, MAX_COUNT);
    }


    @Override
    public List<String> findWords(String dictionaryName, final String prefixToFind, final int max) {
        Validate.isTrue(max <= MAX_COUNT);
        String prefix = prefixToFind.toLowerCase();

        List<String> results = new ArrayList<String>();

        Long start = getDictionary(dictionaryName).rank(prefix);

        //if start is null then check if this is a complete word and try to find it
        if (start == null) {
            start = getDictionary(dictionaryName).rank(prefix + END_TOKEN);
        }

        LOGGER.info("Rank of prefix [{}] was [{}]", prefix, start);

        if (start != null) {
            while (true) {
                Set<String> rangeRecs = getDictionary(dictionaryName).range(start, start + MAX_TRANS_UNIT - 1);

                start += MAX_TRANS_UNIT;

                if (CollectionUtils.isNotEmpty(rangeRecs)) {
                    for (String entry : rangeRecs) {

                        LOGGER.debug("Processing entry [{}]", entry);
                        //If the entry is shorter than prefix then a new sequence has started
                        //Also if entry doesn't have the same prefix
                        if (entry.length() < prefix.length()
                                || entry.substring(0, prefix.length()).equals(prefix) == false
                                || results.size() > max) {
                            LOGGER.info("Returning at prefix [{}]; end condition satisfied; ", entry);
                            return results;

                        } else if (entry.endsWith(END_TOKEN)) {
                            //Remove the end token before returning
                            String word = entry.substring(0, entry.length() - END_TOKEN.length());
                            results.add(word);
                            LOGGER.debug("Found word [{}]", word);
                        } else {
                            LOGGER.debug("Prefix found [{}]", entry);
                        }
                    }
                } else {
                    LOGGER.info("Returning as no more to read");
                    return results;
                }
            }
        }
        return results;
    }
    
    public void setStringRedisTemplate(RedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

}
