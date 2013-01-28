package com.knaptus.oss.redis.dictionary;

import com.knaptus.oss.redis.dictionary.config.RepositoryConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Unit test.
 *
 * @author Aditya Bhardwaj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class})
@ActiveProfiles("test")
public class RedisWordCompletionDictionaryTest extends AbstractRepositoryTest {

    @Inject
    private RedisWordCompletionDictionary redisWordCompletionDictionary;


    @Test
    public void testAddAndFindWords() throws Exception {

        List<String> results = redisWordCompletionDictionary.findWords("tmp", "ba");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.contains("bar"));
        assertTrue(results.contains("barfoo"));

        results = redisWordCompletionDictionary.findWords("tmp", "fo");
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.contains("foo"));
        assertTrue(results.contains("foobar"));

        results = redisWordCompletionDictionary.findWords("tmp", "fo", 1);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.contains("foo"));
    }

    @Test
    public void testFindByCompleteWords() throws Exception {

        List<String> results = redisWordCompletionDictionary.findWords("tmp", "foo");
        assertEquals(2, results.size());
        assertTrue(results.contains("foo"));
        assertTrue(results.contains("foobar"));

        results = redisWordCompletionDictionary.findWords("tmp", "foobar");
        assertEquals(1, results.size());
        assertTrue(results.contains("foobar"));
    }


    @Before
    public void setupData() {
        redisWordCompletionDictionary.addWord("tmp", "foo");
        redisWordCompletionDictionary.addWord("tmp", "bar");
        redisWordCompletionDictionary.addWord("tmp", "foobar");
        redisWordCompletionDictionary.addWord("tmp", "Barfoo");
    }

}
