package com.mby.wiki.controller;

import com.mby.wiki.domain.Test;
import com.mby.wiki.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/test")
public class TestControl {

    private static final Logger LOG = LoggerFactory.getLogger(UserControl.class);

    @Value("${this.hello:test33}")
    private String hello;//获取配置文件的自定义配置  读不到就会选择后面的默认值

    @Resource
    private TestService testService;

    @Resource
    private RedisTemplate redisTemplate;


    /*
    * GET ,POST,PUT,DELETE
    * */
    /*@GetMapping("/hello")
    @PostMapping("/hello")
    @DeleteMapping()
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @RequestMapping(value = "/hello",method = RequestMethod.POST)....*/
    @RequestMapping(value = "/hello")  //访问的接口路径 表示这个接口支持所有的请求方式
    public String hello(){
        return "Hello World"+hello;
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World"+name;
    }

    @RequestMapping("/test/list")  //接口支持所有的请求方式
    public List<Test> list(){
        return testService.list();
    }

    @RequestMapping("/redis/set/{key}/{value}")
    public String set(@PathVariable Long key, @PathVariable String value) {
        redisTemplate.opsForValue().set(key, value, 3600, TimeUnit.SECONDS);
        LOG.info("key: {}, value: {}", key, value);
        return "success";
    }

    @RequestMapping("/redis/get/{key}")
    public Object get(@PathVariable Long key) {
        Object object = redisTemplate.opsForValue().get(key);
        LOG.info("key: {}, value: {}", key, object);
        return object;
    }



}
