package com.intelladept.oss.redis.dictionary;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.data.redis.support.collections.DefaultRedisSet;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Allows operations to retrieve data based on keywords
 *
 * @author Aditya Bhardwaj
 */
@Named
public class RedisKeywordDataRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeywordDataRepository.class);

    private static final String TEMP_SET_KEY = "global:tmpId";
    private static final String TEMP_PREFIX = "tmp:";

    @Inject
    private RedisTemplate stringRedisTemplate;
    
    private RedisDictionary redisDictionary;

    private RedisAtomicLong tempSetKeyCounter;

    @Inject
    public RedisKeywordDataRepository(List<RedisDictionary> redisDictionaries) {
        redisDictionary = new AggregatingRedisDictionary(redisDictionaries);
    }

    @PostConstruct
    public void init() {
        tempSetKeyCounter = new RedisAtomicLong(TEMP_SET_KEY, stringRedisTemplate.getConnectionFactory());
    }

    private String getKeyForKeyword(String type, String keyword) {
        return "index:" + type + ":" + keyword;
    }

    private Set<String> getSetByKeyword(String type, String keyword) {
        return new DefaultRedisSet<String>(getKeyForKeyword(type, keyword), stringRedisTemplate);
    }
    
    public <T> void indexDataByKeywords(String type, KeywordAndIdExtractor<T> keywordAndIdExtractor, T data) {
        Set<String> keywords = keywordAndIdExtractor.extractKeywords(data);

        if (keywords != null) {
            for(String keyword : keywords) {
                getSetByKeyword(type, keyword).add(keywordAndIdExtractor.extractId(data));
                
                redisDictionary.addWord(type, keyword);
            }
        }

    }
    
    public List<String> findDataByPhrase(String type, String phrase) {
        return findDataByKeywordPrefixes(type, StringUtils.split(phrase));
    }

    public List<String> findDataByKeywordPrefixes(String type, String... keywordPrefixes) {
        List<String> resultIds = new ArrayList<String>();
        
        if(keywordPrefixes != null) {
            StringBuilder tmpKeyPrefix = new StringBuilder(TEMP_PREFIX)
                    .append(tempSetKeyCounter.incrementAndGet())
                    .append(":");

            int count = 0;
            List<String> tmpSetKeys = new ArrayList<String>();
            for (String keywordPrefix : keywordPrefixes) {
                LOGGER.debug("Finding data for keyword prefix [{}]", keywordPrefix);

                List<String> resultWords = redisDictionary.findWords(type, keywordPrefix);

                if(CollectionUtils.isNotEmpty(resultWords)) {

                    List<String> setKeys = new ArrayList<String>();
                    for(String resultWord : resultWords) {
                        setKeys.add(getKeyForKeyword(type, resultWord));
                    }
                    
                    if (setKeys.size() == 1) {
                        tmpSetKeys.add(setKeys.get(0));
                    } else if (setKeys.size() > 1) {
                        //Join result from all words found into tmp set
                        String destKey = tmpKeyPrefix.toString() + count++;
                        tmpSetKeys.add(destKey);
                        
                        LOGGER.debug("Words found for prefix [{}]; [{}], destKey [{}]", new Object[]{keywordPrefix, resultWords, destKey});
                        stringRedisTemplate.opsForSet().unionAndStore(setKeys.remove(0), setKeys, destKey);
                    }
                } else {
                    LOGGER.info("No results found for [{}]", keywordPrefix);
                }
            }
            
            if(tmpSetKeys.size() == 1) {
                resultIds.addAll(stringRedisTemplate.opsForSet().members(tmpSetKeys.get(0)));
            } else if(tmpSetKeys.size() > 1) {
                String firstKey = tmpSetKeys.remove(0);
                resultIds.addAll(stringRedisTemplate.opsForSet().intersect(firstKey, tmpSetKeys));


                tmpSetKeys.add(firstKey);
            } else {
                LOGGER.debug("No results found");
            }

            LOGGER.debug("Deleting tmp keys. Ids of the result were [{}]", resultIds);
            //clean up tmp sets
            for(String key : tmpSetKeys) {
                if (key.startsWith(tmpKeyPrefix.toString()))  {
                    stringRedisTemplate.delete(key);
                }
            }

        }

        return resultIds;
    }
    
    private static class AggregatingRedisDictionary implements RedisDictionary {
        
        private List<RedisDictionary> redisDictionaries;
        
        public AggregatingRedisDictionary(List<RedisDictionary> redisDictionaries) {
            Validate.notNull(redisDictionaries);
            this.redisDictionaries = redisDictionaries;    
        }

        @Override
        public void addWord(String type, String wordToSave) {
            for(RedisDictionary redisDictionary : redisDictionaries) {
                redisDictionary.addWord(type, wordToSave);
            }
        }

        @Override
        public List<String> findWords(String type, String prefix) {
            List<String> results = new ArrayList<String>();
            
            for(RedisDictionary redisDictionary : redisDictionaries) {
                results.addAll(redisDictionary.findWords(type, prefix));
            }
            
            return results;
        }

        @Override
        public List<String> findWords(String type, String prefixToFind, int max) {
            List<String> results = new ArrayList<String>();

            for(RedisDictionary redisDictionary : redisDictionaries) {
                results.addAll(redisDictionary.findWords(type, prefixToFind, max));
            }

            return results;
        }
    }

    public void setStringRedisTemplate(RedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

}
