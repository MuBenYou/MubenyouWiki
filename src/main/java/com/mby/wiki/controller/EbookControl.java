package com.mby.wiki.controller;

import com.mby.wiki.req.EbookQueryReq;
import com.mby.wiki.req.EbookSaveReq;
import com.mby.wiki.resp.CommonResp;
import com.mby.wiki.resp.EbookQueryResp;
import com.mby.wiki.resp.PageResp;
import com.mby.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

//@Controller  返回页面 @RestController 是返回字符串的
@RestController  //@ResponseBody用来返回字符串或JSON对象 大多是JSON对象
@RequestMapping("/ebook")
public class EbookControl {
    @Resource
    private EbookService ebookService;

    @RequestMapping("/list")  //接口支持所有的请求方式
    public CommonResp list(EbookQueryReq req){
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list= ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req){
        CommonResp resp = new CommonResp();
        ebookService.save(req);
        return resp;
    }

    @DeleteMapping("/delete/{id}")
    public CommonResp delete(@PathVariable long id){
        CommonResp resp = new CommonResp();
        ebookService.delete(id);
        return resp;
    }
}
