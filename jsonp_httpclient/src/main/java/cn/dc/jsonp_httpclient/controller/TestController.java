package cn.dc.jsonp_httpclient.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * traps
 * callback属性必须不然new JSONPObject(callback, list);会报错
 */
@RestController
public class TestController {

    @RequestMapping("jsonp")
    public JSONPObject jsonp(String callback) {
        List<String> list = Stream.of("123", "234", "qwe").collect(toList());
        JSONPObject jsonpObject = new JSONPObject(callback, list);
        System.out.println(String.format("%1$s,%2$s", callback, jsonpObject));

        return jsonpObject;
    }

    @RequestMapping("httpclient")
    public String testHttpClient() {
        RestTemplate restTemplate = new RestTemplate();
        String str = restTemplate.getForObject("http://localhost:8080/jsonp?callback=callback", String.class);

        System.out.println(str);
        return str;
    }
}
