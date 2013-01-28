package com.knaptus.oss.redis.dictionary;

import com.knaptus.oss.redis.dictionary.config.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

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
            public String extractKeywords(String data) {
                return data;
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

        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "ba");
        LOGGER.info("ids found for ba [{}]", ids);
        assertEquals(2, ids.size());

    }

    @Test
    public void testIndexDataByKeywordsMultiple() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "ba fo");
        LOGGER.info("ids found for ba and fo [{}]", ids);
        assertEquals(1, ids.size());

    }

    @Test
    public void testIndexDataByCompleteKeyword() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "bar");
        LOGGER.info("ids found for ba and fo [{}]", ids);
        assertEquals(2, ids.size());
    }

    @Test
    public void testIndexDataByCompleteKeywordsMultiple() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "bar foo");
        LOGGER.info("ids found for ba and fo [{}]", ids);
        assertEquals(1, ids.size());
    }

    @Test
    public void testIndexDataByCompleteKeywordsMultipleWithMetaphone() throws Exception {
        setupTestData();

        List<String> ids = redisKeywordDataRepository.findDataByPhrase("tmp", "bar fu");
        LOGGER.info("ids found for ba and fo [{}]", ids);
        assertEquals(1, ids.size());
    }

    private void setupTestData() {
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "foo");
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "bar");
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "foo bar");
        redisKeywordDataRepository.indexDataByKeywords("tmp", stringKeywordAndIdExtractor, "random");
    }
}
