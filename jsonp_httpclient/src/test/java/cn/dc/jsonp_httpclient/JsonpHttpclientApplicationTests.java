package cn.dc.jsonp_httpclient;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JsonpHttpclientApplicationTests {
    /*
    测试logback
     */
    @Test
    void contextLoads() {
        Logger logger = LoggerFactory.getLogger(JsonpHttpclientApplicationTests.class);
        Logger logger1 = LoggerFactory.getLogger("root");
        Logger logger2 = LoggerFactory.getLogger("123");
        logger1.info("@@@@@@@@@@@@123" );
        logger2.error("#########123");
    }
    /**
     * data-jpa
     */
    @Test
    void testDataJpa(){

    }
}
