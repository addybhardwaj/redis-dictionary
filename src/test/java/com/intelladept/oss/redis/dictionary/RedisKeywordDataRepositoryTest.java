package com.intelladept.oss.redis.dictionary;

import com.intelladept.oss.redis.dictionary.config.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

/**
 * Unit test.
 *
 * @author Aditya Bhardwaj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class})
@ActiveProfiles("test")
public class RedisKeywordDataRepositoryTest extends AbstractRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeywordDataRepositoryTest.class);

    @Inject
    private RedisKeywordDataRepository redisKeywordDataRepository;

    private KeywordAndIdExtractor<String> stringKeywordAndIdExtractor;

    @Before
    public void before() {
        stringKeywordAndIdExtractor = new KeywordAndIdExtractor<String>() {

            @Override
            public Set<String> extractKeywords(String data) {
                String[] split = data.split(" ");
                LOGGER.info("Keywords [{}]", split);

                return new HashSet<String>(Arrays.asList(split));
            }

            @Override
            public String extractId(String data) {
                return data;
            }
        };
    }

    @Test
    public void testFindEmptyPhrase() throws Exception {
        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", null);
        assertEquals(0, ids.size());
    }

    @Test
    public void testFindEmptySet() throws Exception {
        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "fo");
        assertEquals(0, ids.size());
    }

    @Test
    public void testIndexAndFindOneRecord() throws Exception {
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "foo car");
        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "fo");
        assertEquals(1, ids.size());
    }

    @Test
    public void testIndexDataByKeywords() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByKeywordPrefixes("ba");
        LOGGER.info("ids found for ba [{}]", ids);

    }

    @Test
    public void testIndexDataByKeywordsMultiple() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByKeywordPrefixes("ba", "fo");
        LOGGER.info("ids found for ba and fo [{}]", ids);

    }

    private void setupTestData() {
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "foo");

        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "bar");

        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "foo bar");

        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "random");
    }
}
