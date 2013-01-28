package com.knaptus.oss.redis.dictionary;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit test.
 *
 * @author Aditya Bhardwaj
 */
public class PhraseSplitterTest {

    private PhraseSplitter phraseSplitter;

    @Before
    public void before() {
        phraseSplitter = new PhraseSplitter();
        phraseSplitter.afterPropertiesSet();
    }


    @Test
    public void testPhrase() {
        assertArrayEquals(new String[]{"name", "shady", "slim"}, phraseSplitter.cleanseAndSplitPhrase("my name is (slim shady)", false).toArray());
        assertArrayEquals(new String[]{"is", "my", "name","s", "shady", "slim"}, phraseSplitter.cleanseAndSplitPhrase("my name is (slim's shady)", true).toArray());
        assertArrayEquals(new String[]{"name", "shady", "slim"}, phraseSplitter.cleanseAndSplitPhrase("my name is (slim's shady),", false).toArray());
        assertArrayEquals(new String[]{"addy", "boy", "good", "name", "shady", "slim"}, phraseSplitter.cleanseAndSplitPhrase("my name is (slim's shady).addy is a good boy", false).toArray());
    }


}

