package com.mby.wiki.controller;

import com.alibaba.fastjson.JSONObject;
import com.mby.wiki.req.UserLoginReq;
import com.mby.wiki.req.UserQueryReq;
import com.mby.wiki.req.UserResetPasswordReq;
import com.mby.wiki.req.UserSaveReq;
import com.mby.wiki.resp.CommonResp;
import com.mby.wiki.resp.PageResp;
import com.mby.wiki.resp.UserLoginResp;
import com.mby.wiki.resp.UserQueryResp;
import com.mby.wiki.service.UserService;
import com.mby.wiki.utils.SnowFlake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/user")
public class UserControl {
    private static final Logger LOG = LoggerFactory.getLogger(UserControl.class);
    @Resource
    private UserService userService;

    @Resource
    private SnowFlake snowFlake;

    @Resource
    private RedisTemplate redisTemplate;

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


    @PostMapping("/reset-password")
    public CommonResp resetPassword(@Valid @RequestBody UserResetPasswordReq req){
        req.setPassword(DigestUtils.md5DigestAsHex(
                req.getPassword().getBytes()));
        CommonResp resp = new CommonResp();
        userService.resetPassword(req);
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@Valid @RequestBody UserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp<UserLoginResp> resp = new CommonResp<>();
        UserLoginResp userLoginResp = userService.login(req);

        Long token = snowFlake.nextId();
        LOG.info("生成单点登录token：{}，并放入redis中", token);
        userLoginResp.setToken(token.toString());
        redisTemplate.opsForValue().set(token.toString(),
                JSONObject.toJSONString(userLoginResp),
                3600 * 24, TimeUnit.SECONDS);

        resp.setContent(userLoginResp);
        return resp;
    }

    @GetMapping("/logout/{token}")
    public CommonResp logout(@PathVariable String token){
        CommonResp resp = new CommonResp();
        redisTemplate.delete(token);
        LOG.info("从redis中删除token:{}", token);
        return resp;
    }

}
