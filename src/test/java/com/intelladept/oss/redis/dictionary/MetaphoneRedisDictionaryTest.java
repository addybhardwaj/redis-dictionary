package com.intelladept.oss.redis.dictionary;

import com.intelladept.oss.redis.dictionary.config.RepositoryConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * Unit test.
 *
 * @author Aditya Bhardwaj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class})
@ActiveProfiles("test")
public class MetaphoneRedisDictionaryTest extends AbstractRepositoryTest {

    @Inject
    private MetaphoneRedisDictionary metaphoneRedisDictionary;

    @Test
    public void testAddWordAndFind() throws Exception {
        metaphoneRedisDictionary.addWord("tmp", "cat");
        metaphoneRedisDictionary.addWord("tmp", "kat");
        metaphoneRedisDictionary.addWord("tmp", "kitty");
        metaphoneRedisDictionary.addWord("tmp", "fitty");
        metaphoneRedisDictionary.addWord("tmp", "factual");

        List<String> results = metaphoneRedisDictionary.findWords("tmp", "kat");
        assertEquals(3, results.size());
        assertArrayEquals(new String[] {"cat", "kat", "kitty"}, results.toArray());

        results = metaphoneRedisDictionary.findWords("tmp", "kat", 2);
        assertEquals(2, results.size());
        assertArrayEquals(new String[] {"cat", "kat"}, results.toArray());

        results = metaphoneRedisDictionary.findWords("tmp", "fctul");
        assertEquals(1, results.size());
    }

}
