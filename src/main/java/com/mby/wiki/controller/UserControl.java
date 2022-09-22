package com.mby.wiki.controller;

import com.mby.wiki.req.UserQueryReq;
import com.mby.wiki.req.UserSaveReq;
import com.mby.wiki.resp.CommonResp;
import com.mby.wiki.resp.UserQueryResp;
import com.mby.wiki.resp.PageResp;
import com.mby.wiki.service.UserService;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/user")
public class UserControl {
    @Resource
    private UserService userService;

    @RequestMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(@Valid UserQueryReq req){
        CommonResp<PageResp<UserQueryResp>> resp = new CommonResp<>();
        PageResp<UserQueryResp> list= userService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@Valid @RequestBody UserSaveReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(
                req.getPassword().getBytes()));
        CommonResp resp = new CommonResp();
        userService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        userService.delete(id);
        return resp;
    }
}
