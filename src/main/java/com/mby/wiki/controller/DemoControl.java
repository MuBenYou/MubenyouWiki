package com.mby.wiki.controller;

import com.mby.wiki.domain.Demo;
import com.mby.wiki.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/demo")
public class DemoControl {
    @Resource
    private DemoService demoService;

    @RequestMapping("/list")  //接口支持所有的请求方式
    public List<Demo> list(){
        return demoService.list();
    }
}
