package com.mby.wiki.controller;

import com.mby.wiki.domain.Test;
import com.mby.wiki.service.TestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
public class TestControl {

    @Value("${this.hello:test33}")
    private String hello;//获取配置文件的自定义配置  读不到就会选择后面的默认值

    @Resource
    private TestService testService;

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
}
