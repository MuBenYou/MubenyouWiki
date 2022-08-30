package com.lrvinglm.miki.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
public class TestControl {
    /*
    * GET ,POST,PUT,DELETE
    * */
    /*@GetMapping("/hello")
    @PostMapping("/hello")
    @DeleteMapping()
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    @RequestMapping(value = "/hello",method = RequestMethod.POST)....*/
    @RequestMapping("/hello")  //访问的接口路径 表示这个接口支持所有的请求方式
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name){
        return "Hello World"+name;
    }
}