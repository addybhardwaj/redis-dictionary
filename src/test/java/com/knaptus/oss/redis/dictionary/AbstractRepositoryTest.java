package com.knaptus.oss.redis.dictionary;

import com.knaptus.oss.redis.dictionary.config.RepositoryConfiguration;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

/**
 * Commons actions for redis repository tests.
 *
 * @author Aditya Bhardwaj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RepositoryConfiguration.class})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractRepositoryTest {

    @Inject
    private RedisConnectionFactory redisConnectionFactory;

    @After
    public void tearDown() {
        redisConnectionFactory.getConnection().flushDb();
//        redisConnectionFactory.getConnection().close();
    }


}
